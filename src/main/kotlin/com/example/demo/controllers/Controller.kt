package com.example.demo.controllers

import com.example.demo.dto.CreateOrderDto
import com.example.demo.dto.OrderDto
import com.example.demo.dto.OrdersDto
import com.example.demo.entities.OrderEntity
import com.example.demo.repositories.OrderRepository
import com.example.demo.repositories.UserRepository
import com.example.demo.services.AuthService
import com.example.demo.services.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*


@RestController
class Controller(
        private val authService: AuthService,
        private val orderRepository: OrderRepository,
        private val mapper: ObjectMapper
) {

    @GetMapping("hello/user")
    fun helloUser(): ResponseEntity<String?>? {
        return ResponseEntity.ok("Hello user")
    }

    @GetMapping("hello/admin")
    fun helloAdmin(): ResponseEntity<String?>? {
        return ResponseEntity.ok("Hello admin")
    }

    @PostMapping("create/order")
    fun createOrder(@RequestBody order: CreateOrderDto): ResponseEntity<String?>? {
        orderRepository.save(OrderEntity().apply {
            description = order.description
            photo = order.photo!!
        })
        return ResponseEntity.ok("Hello admin")
    }

    @GetMapping("get/orders")
    fun getOrders(): String? {
        val ordersDto = OrdersDto()
        orderRepository.findAll().forEach {
            ordersDto.orders!!.add(OrderDto(it.id, it.description!!, it.photo!!))
        }
        return Gson().toJson(ordersDto)
    }
}