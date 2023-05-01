package com.example.demo.components

import com.example.demo.dto.enums.RoleEnum
import org.springframework.security.core.Authentication


class JwtAuthentication : Authentication {
    private var authenticated = false
    private var username: String? = null
    private var firstName: String? = null
    private var roles: Set<RoleEnum>? = null

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

    fun setRoles(roles: ArrayList<String>) {
        val setRoles: MutableSet<RoleEnum> = mutableSetOf()
        for (role in roles) {
            setRoles.add(RoleEnum.valueOf(role))
        }

        this.roles = setRoles
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun setFirstName(firstName: String) {
        this.firstName = firstName
    }

    override fun getAuthorities(): Set<RoleEnum>? {
        return roles
    }

}