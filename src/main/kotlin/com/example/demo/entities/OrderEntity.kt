package com.example.demo.entities

import com.example.demo.dto.enums.RoleEnum
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

    var description: String? = null

    var photo: ByteArray? = null

}