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

    @OneToOne(mappedBy="order")
    var photo: PhotoEntity? = null

    var status: String? = null

    var price: String? = null

    var track: String? = null

    var paymentAddress: String? = null

    var address: String? = null

    @ManyToOne
    @JoinColumn(name="users_id", nullable=false)
    var user: UserEntity? = null

    @OneToOne(mappedBy="order")
    var video: VideoEntity? = null

    @OneToOne(mappedBy="order")
    var model: ModelEntity? = null

}