package com.example.demo.entities

import com.example.demo.dto.enums.RoleEnum
import org.hibernate.criterion.Order
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
@Table(name="photos")
class PhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

    var photo: ByteArray? = null

    @OneToOne
    @JoinColumn(name="orders_id", nullable=true)
    var order: OrderEntity? = null



}