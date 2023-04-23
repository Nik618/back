package com.example.demo.components

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
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
            //val claims = jwtProviderComponent.getAccessClaims(token)
            val jwtInfoToken = JwtAuthentication()
            jwtInfoToken.isAuthenticated = true
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

}