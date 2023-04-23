package com.example.demo.services

import com.example.demo.components.JwtProviderComponent
import com.example.demo.dto.JwtRequestDto
import com.example.demo.dto.JwtResponseDto
import com.example.demo.dto.UserDto
import com.example.demo.repositories.UserRepository
import io.jsonwebtoken.Claims
import org.springframework.lang.NonNull
import org.springframework.stereotype.Service
import java.util.function.Supplier
import javax.security.auth.message.AuthException


@Service
class AuthService(
        private val userService: UserService,
        private val jwtProviderComponent: JwtProviderComponent,
        private val userRepository: UserRepository
) {

    private val refreshStorage: MutableMap<String, String> = HashMap()

    fun login(@NonNull authRequest: JwtRequestDto): JwtResponseDto? {
        val user: UserDto = userService.getByLogin(authRequest.login!!)!!.orElseThrow(Supplier { AuthException("Пользователь не найден") })
        println(user.login)
        return if (user.password.equals(authRequest.password)) {
            val accessToken = jwtProviderComponent.generateAccessToken(user)
            val refreshToken = jwtProviderComponent.generateRefreshToken(user)
            //refreshStorage[user.login!!] = refreshToken!!
            val userEntity = userRepository.findByUsername(user.login!!)
            userEntity.refreshToken = refreshToken
            userRepository.save(userEntity)
            JwtResponseDto("Bearer", accessToken!!, refreshToken)
        } else {
            throw AuthException("Неправильный пароль")
        }
    }

    fun getAccessToken(@NonNull refreshToken: String): JwtResponseDto? {
        if (jwtProviderComponent.validateRefreshToken(refreshToken)) {
            val claims: Claims = jwtProviderComponent.getRefreshClaims(refreshToken)!!
            val login = claims.subject
            val saveRefreshToken = userRepository.findByUsername(login).refreshToken
            if (saveRefreshToken != null && saveRefreshToken == refreshToken) {
                val user: UserDto = userService.getByLogin(login)!!
                        .orElseThrow(Supplier { AuthException("Пользователь не найден") })
                val accessToken = jwtProviderComponent.generateAccessToken(user)!!
                val refreshTokenNew = jwtProviderComponent.generateRefreshToken(user)
                val userEntity = userRepository.findByUsername(user.login!!)
                userEntity.refreshToken = refreshTokenNew
                userRepository.save(userEntity)
                return JwtResponseDto("Bearer", accessToken, refreshTokenNew)
            }
        }
        return JwtResponseDto(null, null)
    }

}