package com.ics0022.passMan.service

import com.ics0022.passMan.model.User
import com.ics0022.passMan.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun registerUser(username: String, rawPassword: String): User {
        val encodedPassword = passwordEncoder.encode(rawPassword)
        val user = User(username = username, password = encodedPassword)
        return userRepository.save(user)
    }

    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }
}
