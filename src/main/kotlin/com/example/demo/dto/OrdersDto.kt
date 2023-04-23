package com.example.demo.dto

data class OrdersDto(
        val orders: MutableList<OrderDto>? = mutableListOf()
)