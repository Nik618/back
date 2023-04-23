package com.example.demo.dto

import com.example.demo.dto.enums.RoleEnum

data class UserDto (
        val login: String? = null,
        val password: String? = null,
        val name: String? = null,
        var role: RoleEnum? = null
)