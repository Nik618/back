package com.example.demo.services

import com.example.demo.dto.UserDto
import com.example.demo.dto.enums.RoleEnum
import com.example.demo.repositories.UserRepository
import org.springframework.lang.NonNull
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Predicate

@Service
class UserService(
        private val userRepository: UserRepository
) {

    fun getByLogin(@NonNull login: String): Optional<UserDto>? {
        val userEntities = userRepository.findAllByUsername(login)
        val users = mutableListOf<UserDto>()
        userEntities.forEach {
            users.add(UserDto(it.username, it.password, it.name, RoleEnum.valueOf(it.role!!)))
        }
        return users.stream()
                .filter(Predicate<UserDto> { user: UserDto -> login == user.login })
                .findFirst()
    }

}