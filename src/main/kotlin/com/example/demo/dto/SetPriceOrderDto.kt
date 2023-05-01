package com.example.demo.dto

import com.example.demo.dto.enums.RoleEnum

data class SetOrderDto (
        val id: Int? = null,
        var price: String? = null,
        val track: String? = null
)