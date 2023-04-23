package com.example.demo.services

import com.example.demo.repositories.UserRepository
import mu.KotlinLogging
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@EnableScheduling
class ScheduleService(
        private val userRepository: UserRepository,
) {
    private val logger = KotlinLogging.logger {}

    @Scheduled(fixedRate = 100000)
    private fun test() {
        logger.info { "Getting users..." }
        userRepository.findAll().forEach {
            logger.info { "->> user id: ${it.id}, login: ${it.username}" }
        }

    }
}