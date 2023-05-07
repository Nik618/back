package com.example.demo.repositories

import com.example.demo.entities.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PhotoRepository: CrudRepository<PhotoEntity, Int> {

    fun existsByOrder(orderEntity: OrderEntity) : Boolean

    fun findByOrder(orderEntity: OrderEntity) : PhotoEntity
}