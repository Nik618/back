package com.example.demo.controllers

import com.example.demo.dto.CreateOrderDto
import com.example.demo.dto.OrderDto
import com.example.demo.dto.OrdersDto
import com.example.demo.dto.SetOrderDto
import com.example.demo.entities.OrderEntity
import com.example.demo.repositories.OrderRepository
import com.example.demo.repositories.UserRepository
import com.example.demo.services.AuthService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
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
        orderRepository.save(OrderEntity().apply {
            description = order.description
            photo = order.photo!!
            status = "NEW"
            user = userRepository.findByUsername(order.user!!)
        })
        return ResponseEntity.ok("Hello admin")
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
            ordersDto.orders!!.add(OrderDto(it.id, it.description!!, bytes, it.status, it.price, it.track))
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
                track = order.track
        )
        return Gson().toJson(orderDto)
    }

    @PostMapping("set/price")
    fun setPrice(@RequestBody orderDto: OrderDto): ResponseEntity<String?>? {
        orderRepository.save(orderRepository.findById(orderDto.id!!).get().apply {
            status = "WAITING TO PAYMENT"
            price = orderDto.price
        })
        return ResponseEntity.ok("setting status successful")
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
}