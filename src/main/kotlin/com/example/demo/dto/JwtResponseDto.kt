package com.example.demo.dto

data class JwtResponseDto(
        val type: String? = null,
        val accessToken: String? = null,
        val refreshToken: String? = null
)