package com.example.demo.dto.enums

import org.springframework.security.core.GrantedAuthority

enum class RoleEnum(s: String) : GrantedAuthority {

    ADMIN("ADMIN"),
    USER("USER");

    private val value: String? = null
    override fun getAuthority(): String {
        return value!!
    }
}