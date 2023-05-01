package com.example.demo.entities

import com.example.demo.dto.enums.RoleEnum
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
@Table(name="orders")
class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

    var description: String? = null

    var photo: ByteArray? = null

    var status: String? = null

    var price: String? = null

    var track: String? = null

    @ManyToOne
    @JoinColumn(name="users_id", nullable=false)
    var user: UserEntity? = null

}