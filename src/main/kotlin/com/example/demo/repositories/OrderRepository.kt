package com.example.demo.repositories

import com.example.demo.entities.OrderEntity
import com.example.demo.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: CrudRepository<OrderEntity, Int> {

    fun findAllByUser(user: UserEntity) : List<OrderEntity>


}