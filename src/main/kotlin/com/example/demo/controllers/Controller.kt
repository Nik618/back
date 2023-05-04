package com.example.demo.controllers

import com.example.demo.dto.CreateOrderDto
import com.example.demo.dto.OrderDto
import com.example.demo.dto.OrdersDto
import com.example.demo.dto.ResponseDto
import com.example.demo.dto.yookassa.request.Amount
import com.example.demo.dto.yookassa.request.Confirmation
import com.example.demo.dto.yookassa.request.PaymentMethodData
import com.example.demo.dto.yookassa.request.YooKassaRequest
import com.example.demo.dto.yookassa.response.YooKassaResponse
import com.example.demo.entities.OrderEntity
import com.example.demo.repositories.OrderRepository
import com.example.demo.repositories.UserRepository
import com.example.demo.services.AuthService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.apache.tomcat.jni.User.username
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.util.*
import javax.imageio.ImageIO


@RestController
class Controller(
        private val authService: AuthService,
        private val orderRepository: OrderRepository,
        private val userRepository: UserRepository,
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
//        val testFile = File("test.${order.extension}")
//        val fos = FileOutputStream(testFile)
//        val bytes = order.file
//        fos.write(bytes!!)
//        fos.close()

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
                id = order.id,
                description = order.description,
                photo = order.photo,
                status = order.status,
                price = order.price,
                track = order.track,
                paymentAddress = order.paymentAddress
        )
        return Gson().toJson(orderDto)
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

    @PostMapping("set/track")
    fun setTrack(@RequestBody orderDto: OrderDto): ResponseEntity<String?>? {
        orderRepository.save(orderRepository.findById(orderDto.id!!).get().apply {
            status = "IN DELIVERY"
            track = orderDto.track
        })
        return ResponseEntity.ok("setting status successful")
    }

    @PostMapping("paid")
    fun paid(@RequestBody orderDto: OrderDto): ResponseEntity<String?>? {
        orderRepository.save(orderRepository.findById(orderDto.id!!).get().apply {
            status = "WAITING FOR PRINTING"
            track = orderDto.track
        })
        return ResponseEntity.ok("setting status successful")
    }

    @PostMapping("printing")
    fun printing(@RequestBody orderDto: OrderDto): ResponseEntity<String?>? {
        orderRepository.save(orderRepository.findById(orderDto.id!!).get().apply {
            status = "PRINTING"
        })
        return ResponseEntity.ok("setting status successful")
    }

    @PostMapping("approve/receiving")
    fun approveReceiving(@RequestBody orderDto: OrderDto): ResponseEntity<String?>? {
        orderRepository.save(orderRepository.findById(orderDto.id!!).get().apply {
            status = "DONE"
            track = orderDto.track
        })
        return ResponseEntity.ok("setting status successful")
    }

    @GetMapping("del/order")
    fun delOrder(@RequestParam id: Int): String {
        println("del/order")
        orderRepository.delete(orderRepository.findById(id).get())
        println("del/order done")
        return Gson().toJson(OrderDto())
    }
}