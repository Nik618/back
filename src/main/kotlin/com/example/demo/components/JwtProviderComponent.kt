package com.example.demo.components

import com.example.demo.dto.UserDto
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.lang.NonNull
import org.springframework.stereotype.Component
import java.security.Key
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Component
class JwtProviderComponent(
        @Value("\${jwt.secret.access}")
        private val jwtAccessSecretString: String,
        @Value("\${jwt.secret.refresh}")
        private val jwtRefreshSecretString: String
) {

    private val jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecretString));
    private val jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecretString));

    fun generateAccessToken(@NonNull user: UserDto): String? {
        val now = LocalDateTime.now()
        val accessExpirationInstant = now.plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant()
        val accessExpiration: Date = Date.from(accessExpirationInstant)
        return Jwts.builder()
                .setSubject(user.login)
                .setExpiration(accessExpiration)
                .signWith(SignatureAlgorithm.HS512, jwtAccessSecret)
                .claim("role", user.role)
                .claim("name", user.name)
                .compact()
    }

    fun generateRefreshToken(@NonNull user: UserDto): String? {
        val now = LocalDateTime.now()
        val refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant()
        val refreshExpiration = Date.from(refreshExpirationInstant)
        return Jwts.builder()
                .setSubject(user.login)
                .setExpiration(refreshExpiration)
                .signWith(SignatureAlgorithm.HS512, jwtRefreshSecret)
                .compact()
    }

    fun validateAccessToken(@NonNull accessToken: String?): Boolean {
        return validateToken(accessToken, jwtAccessSecret)
    }

    fun validateRefreshToken(@NonNull refreshToken: String?): Boolean {
        return validateToken(refreshToken, jwtRefreshSecret)
    }

    private fun validateToken(@NonNull token: String?, @NonNull secret: Key?): Boolean {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
            return true
        } catch (expEx: ExpiredJwtException) {
            println("Token expired")
        } catch (unsEx: UnsupportedJwtException) {
            println("Unsupported jwt")
        } catch (mjEx: MalformedJwtException) {
            println("Malformed jwt")
        } catch (sEx: SignatureException) {
            println("Invalid signature")
        } catch (e: Exception) {
            println("invalid token")
        }
        return false
    }

    fun getAccessClaims(@NonNull token: String): Claims? {
        return getClaims(token, jwtAccessSecret)
    }

    fun getRefreshClaims(@NonNull token: String): Claims? {
        return getClaims(token, jwtRefreshSecret)
    }

    private fun getClaims(@NonNull token: String, @NonNull secret: Key): Claims? {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .body
    }
}