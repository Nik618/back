package com.example.demo.controllers

import com.example.demo.dto.*
import com.example.demo.dto.yookassa.request.Amount
import com.example.demo.dto.yookassa.request.Confirmation
import com.example.demo.dto.yookassa.request.PaymentMethodData
import com.example.demo.dto.yookassa.request.YooKassaRequest
import com.example.demo.dto.yookassa.response.YooKassaResponse
import com.example.demo.entities.OrderEntity
import com.example.demo.entities.VideoEntity
import com.example.demo.repositories.OrderRepository
import com.example.demo.repositories.UserRepository
import com.example.demo.repositories.VideoRepository
import com.example.demo.services.AuthService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.net.URI
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO


@RestController
class Controller(
        private val authService: AuthService,
        private val orderRepository: OrderRepository,
        private val userRepository: UserRepository,
        private val videoRepository: VideoRepository,
        private val mapper: ObjectMapper
) {

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("hello/user")
    fun helloUser(): ResponseEntity<String?>? {
        return ResponseEntity.ok("Hello user")
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("hello/admin")
    fun helloAdmin(): ResponseEntity<String?>? {
        return ResponseEntity.ok("Hello admin")
    }


    @PostMapping("create/order")
    fun createOrder(@RequestBody order: CreateOrderDto): ResponseEntity<String?>? {
        orderRepository.save(OrderEntity().apply {
            description = order.description
            photo = order.photo
            file = order.file
            extension = order.extension
            mimeType = order.mimeType
            status = "NEW"
            user = userRepository.findByUsername(order.user!!)
        })
        return ResponseEntity.ok("Hello admin")
    }


    @GetMapping("get/file")
    fun getFile(@RequestParam id: Int): String? {
        println("get/file")
        val orderEntity = orderRepository.findById(id).get()
        val orderDto = OrderDto(
                file = orderEntity.file,
                extension = orderEntity.extension,
                mimeType = orderEntity.mimeType
        )
        return Gson().toJson(orderDto)
    }


    @GetMapping("stop/video")
    fun stopVideo(@RequestParam id: Int): String? {
        println("stop/video")
        val orderEntity = orderRepository.findById(id).get()
        if (videoRepository.existsByOrder(orderEntity)) {
            val videoEntity = videoRepository.findByOrder(orderEntity)
            val pid = videoEntity.pid
            val processBuilder = ProcessBuilder().redirectErrorStream(true)
            processBuilder.command("bash", "-c", "kill $pid")
            val process = processBuilder.start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null)
                println(line)
            // process.waitFor().toString()
            videoRepository.delete(videoEntity)
            orderRepository.save(orderEntity.apply {
                status = "PREPARE TO DELIVERY"
            })
            return Gson().toJson(ResultDto(
                    result = "Video was stopped",
                    status = "success")
            )
        } else
            return Gson().toJson(ResultDto(
                    status = "error",
                    errorMesssage = "No video with this id")
            )
    }


    @PostMapping("start/video")
    fun startVideo(@RequestBody startVideoDto: StartVideoDto): String? {
        println("start/video")

        if (videoRepository.findAllByPathId(startVideoDto.pathId!!).isNotEmpty()) {
            return Gson().toJson(ResultDto(
                    status = "error",
                    errorMesssage = "This pathId already use!")
            )
        }

        val executor = Executors.newSingleThreadExecutor()
        val callable: Callable<Pair<Int, Long>> = Callable<Pair<Int, Long>> {
            var exitCode = 200
            val processBuilder = ProcessBuilder().redirectErrorStream(true)
            processBuilder.command("bash", "-c", "ffmpeg -f v4l2 -framerate 24 -video_size 480x480 -i /dev/${startVideoDto.cameraId} -f rtsp -rtsp_transport tcp rtsp://localhost:8554/${startVideoDto.pathId}")
            println("\"ffmpeg -f v4l2 -framerate 24 -video_size 480x480 -i /dev/${startVideoDto.cameraId} -f rtsp -rtsp_transport tcp rtsp://localhost:8554/${startVideoDto.pathId}")

            val process = processBuilder.start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))

            var line: String?

            var count = 30
            while (reader.readLine().also { line = it } != null && count != 0) {
                println(line)
                count--
            }

            println("....... text shadowed .......")
            println(process.pid())
            if (process.waitFor(2, TimeUnit.SECONDS)) {
                exitCode = process.waitFor()
            }
            return@Callable Pair(exitCode, process.pid())
        }

        val threadResponse = executor.submit(callable)
        if (threadResponse.get().first == 200) {
            val orderEntity = orderRepository.findById(startVideoDto.orderId!!).get()
            videoRepository.save(VideoEntity().apply {
                cameraId = startVideoDto.cameraId
                pathId = startVideoDto.pathId
                pid = threadResponse.get().second
                path = "rtsp://feivur.ru:8554/${startVideoDto.pathId}"
                order = orderEntity
            })
            orderRepository.save(orderEntity.apply {
                status = "PRINTING"
            })
            return Gson().toJson(ResultDto(
                    result = "rtsp://feivur.ru:8554/${startVideoDto.pathId}",
                    status = "success")
            )
        } else
            return Gson().toJson(ResultDto(
                    status = "error",
                    errorMesssage = "Error starting video: camera is busy or disconnect with server")
            )

    }


    @GetMapping("get/orders")
    fun getOrders(@RequestParam user: String): String? {

        val ordersDto = OrdersDto()
        val listOrders = if (user == "") orderRepository.findAll() else orderRepository.findAllByUser(userRepository.findByUsername(user))
        listOrders.forEach {
            val originalImage: BufferedImage = ImageIO.read(it.photo!!.inputStream())
            val resizedImage = BufferedImage(50, 50, originalImage.type)
            val g = resizedImage.createGraphics()
            g.drawImage(originalImage, 0, 0, 50, 50, null)
            g.dispose()
            val baos = ByteArrayOutputStream()
            ImageIO.write(resizedImage, "jpg", baos)
            val bytes = baos.toByteArray()
            ordersDto.orders!!.add(OrderDto(it.id, it.description!!, bytes, null, null, null, it.status, it.price, it.track, it.paymentAddress))
        }
        return Gson().toJson(ordersDto)
    }

    @GetMapping("get/order")
    fun getOrder(@RequestParam id: Int): String? {
        val order = orderRepository.findById(id).get()
        val orderDto = OrderDto(
                status = order.status,
                price = order.price,
                track = order.track,
                paymentAddress = order.paymentAddress
        )
        return Gson().toJson(orderDto)
    }

    @GetMapping("get/status")
    fun getStatus(@RequestParam id: Int): String? {
        val order = orderRepository.findById(id).get()
        return Gson().toJson(ResultDto(
                result = order.status,
                status = "success"
        ))
    }

    @GetMapping("get/photo")
    fun getPhoto(@RequestParam id: Int): String? {
        val order = orderRepository.findById(id).get()

        return Gson().toJson(OrderDto(photo = order.photo))
    }

    @PostMapping("set/price")
    fun setPrice(@RequestBody orderDto: OrderDto): String? {
        println("set/price")
        val yooKassaRequest = YooKassaRequest(
                Amount(value = orderDto.price, currency = "RUB"),
                PaymentMethodData(type = "bank_card"),
                Confirmation(type = "redirect", returnUrl = "https://www.example.com/return_url"),
                description = "Тестовый заказ"
        )

        val encodedAuth = Base64.getEncoder().encodeToString("315027:test_NZ_z8nBR3bTU5A2oFFnwQoTtnxGR26FqItm8lghOmn0".toByteArray())
        val httpHeaders = HttpHeaders()
        httpHeaders.set("Authorization", "Basic $encodedAuth")
        httpHeaders.set("Content-Type", "application/json")
        httpHeaders.set("Idempotence-Key", UUID.randomUUID().toString())
        val requestEntity = HttpEntity<String>(Gson().toJson(yooKassaRequest), httpHeaders)
        val response = RestTemplate().postForEntity(URI("https://api.yookassa.ru/v3/payments"), requestEntity, String::class.java)
        val yooKassaResponse = Gson().fromJson(response.body, YooKassaResponse::class.java)
        println(response.body)

        orderRepository.save(orderRepository.findById(orderDto.id!!).get().apply {
            status = "WAITING TO PAYMENT"
            price = orderDto.price
            paymentAddress = yooKassaResponse.confirmation?.confirmationUrl
        })

        return Gson().toJson(yooKassaResponse)
    }

    @GetMapping("get/track")
    fun getTrack(@RequestParam id: Int): String? {
        val track = orderRepository.findById(id).get().track
        return Gson().toJson(ResultDto(
                result = track,
                status = "success")
        )
    }


    @PostMapping("approve/receiving")
    fun approveReceiving(@RequestBody orderDto: OrderDto): String? {
        orderRepository.save(orderRepository.findById(orderDto.id!!).get().apply {
            status = "DONE"
        })
        return Gson().toJson(ResultDto(
                status = "success"
        )
        )
    }

    @PostMapping("prepare/delivery")
    fun prepareToDelivery(@RequestBody orderDto: OrderDto): String? {
        orderRepository.save(orderRepository.findById(orderDto.id!!).get().apply {
            status = "IN DELIVERY"
            track = orderDto.track
        })
        return Gson().toJson(ResultDto(
                status = "success")
        )
    }

    @GetMapping("del/order")
    fun delOrder(@RequestParam id: Int): String {
        println("del/order")
        orderRepository.delete(orderRepository.findById(id).get())
        println("del/order done")
        return Gson().toJson(ResultDto(
                status = "success"
        ))
    }

    @GetMapping("get/video")
    fun getVideo(@RequestParam id: Int): String {
        println("get/video")
        val orderEntity = orderRepository.findById(id).get()
        val result = videoRepository.findByOrder(orderEntity).path
        return Gson().toJson(ResultDto(
                result = result,
                status = "success")
        )
    }
}