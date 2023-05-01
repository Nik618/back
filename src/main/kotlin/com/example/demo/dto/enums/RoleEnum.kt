package com.example.demo.dto.enums

import lombok.RequiredArgsConstructor
import org.springframework.security.core.GrantedAuthority

@RequiredArgsConstructor
enum class RoleEnum : GrantedAuthority {

    ADMIN,
    USER;

    override fun getAuthority(): String {
        return name
    }
}