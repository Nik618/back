package com.example.demo.dto

import java.security.cert.Extension

data class StartVideoDto(
        val orderId: Int? = null,
        val cameraId: String? = null,
        val pathId: String? = null,
)