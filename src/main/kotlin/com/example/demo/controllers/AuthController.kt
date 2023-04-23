package com.example.demo.controllers

import com.example.demo.dto.JwtRequestDto
import com.example.demo.dto.JwtResponseDto
import com.example.demo.dto.RefreshJwtRequestDto
import com.example.demo.dto.UserDto
import com.example.demo.dto.enums.RoleEnum
import com.example.demo.entities.UserEntity
import com.example.demo.repositories.UserRepository
import com.example.demo.services.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class AuthController(
        private val authService: AuthService,
        private val userRepository: UserRepository
) {
    @PostMapping("login")
    fun login(@RequestBody authRequest: JwtRequestDto): ResponseEntity<JwtResponseDto?>? {
        val token: JwtResponseDto? = authService.login(authRequest)
        return ResponseEntity.ok<JwtResponseDto?>(token)
    }

    @PostMapping("token")
    fun getNewAccessToken(@RequestBody request: RefreshJwtRequestDto): ResponseEntity<JwtResponseDto?>? {
        val token: JwtResponseDto? = authService.getAccessToken(request.refreshToken!!)
        return ResponseEntity.ok<JwtResponseDto?>(token)
    }

    @PostMapping("sign")
    fun sign(@RequestBody request: UserDto): ResponseEntity<String> {
        userRepository.save(UserEntity().apply {
            username = request.login
            name = request.name
            password = request.password
            role = "USER"
        })
        return ResponseEntity.ok("200 OK")
    }


}