package com.example.demo.repositories

import com.example.demo.entities.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: CrudRepository<UserEntity, Int> {
    fun findByUsername(username: String) : UserEntity
    fun findAllByUsername(username: String) : List<UserEntity>
    fun existsByUsername(username: String): Boolean
}