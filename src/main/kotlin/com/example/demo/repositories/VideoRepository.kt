package com.example.demo.repositories

import com.example.demo.entities.OrderEntity
import com.example.demo.entities.UserEntity
import com.example.demo.entities.VideoEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface VideoRepository: CrudRepository<VideoEntity, Int> {
    fun findAllByPathId(pathId: String) : List<VideoEntity>

    fun existsByOrder(orderEntity: OrderEntity) : Boolean

    fun findByOrder(orderEntity: OrderEntity) : VideoEntity
}