package com.example.demo.components

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority




class JwtAuthentication : Authentication {
    private var authenticated = false
    private val username: String? = null
    private val firstName: String? = null

    override fun getCredentials(): Any? {
        return null
    }

    override fun getDetails(): Any? {
        return null
    }

    override fun getPrincipal(): Any? {
        return username
    }

    override fun isAuthenticated(): Boolean {
        return authenticated
    }

    @Throws(IllegalArgumentException::class)
    override fun setAuthenticated(isAuthenticated: Boolean) {
        authenticated = isAuthenticated
    }

    override fun getName(): String? {
        return firstName
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        TODO("Not yet implemented")
    }

}