package com.example.demo.entities

import com.example.demo.dto.enums.RoleEnum
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

    var username: String? = null

    var password: String? = null

    var name: String? = null

    val accessToken: String? = null

    var refreshToken: String? = null

    var role: String? = null

}