package com.example.demo.components

import com.example.demo.dto.enums.RoleEnum
import io.jsonwebtoken.Claims
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import java.util.stream.Collectors
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest


@Component
class JwtFilterComponent(
        private val jwtProviderComponent: JwtProviderComponent
) : GenericFilterBean() {
    private val AUTHORIZATION = "Authorization"

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse?, fc: FilterChain) {
        val token = getTokenFromRequest(request as HttpServletRequest)
        if (token != null && jwtProviderComponent.validateAccessToken(token)) {
            val claims = jwtProviderComponent.getAccessClaims(token)
            val jwtInfoToken = generate(claims!!)
            jwtInfoToken!!.isAuthenticated = true
            SecurityContextHolder.getContext().authentication = jwtInfoToken
        }
        fc.doFilter(request, response)
    }

    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        val bearer = request.getHeader(AUTHORIZATION)
        return if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            bearer.substring(7)
        } else null
    }


    fun generate(claims: Claims): JwtAuthentication? {
        val jwtInfoToken = JwtAuthentication()
        println(claims)
        jwtInfoToken.setRoles(claims["roles"] as ArrayList<String>)
        jwtInfoToken.setFirstName(claims["sub", String::class.java])
        jwtInfoToken.setUsername(claims.subject)
        return jwtInfoToken
    }



}