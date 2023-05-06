package com.example.demo.entities

import com.example.demo.dto.enums.RoleEnum
import org.hibernate.criterion.Order
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
@Table(name="videos")
class VideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

    var cameraId: String? = null

    var pathId: String? = null

    var pid: Long? = null

    var path: String? = null

    @OneToOne
    @JoinColumn(name="orders_id", nullable=false)
    var order: OrderEntity? = null



}