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
    class UserAlreadyExistsException(message: String) : RuntimeException(message)
    class InvalidInputException(message: String) : RuntimeException(message)
    fun registerUser(username: String, rawPassword: String): User {
        val existingUser = userRepository.findByUsername(username)
        if (existingUser != null) {
            throw UserAlreadyExistsException("User already exists")
        }
        if (username.isEmpty() || rawPassword.isEmpty()) {
            throw InvalidInputException("Username and password cannot be empty")
        } else if (username.length < 3 || rawPassword.length < 3) {
            throw InvalidInputException("Username and password must be at least 3 characters long")
        }
        val encodedPassword = passwordEncoder.encode(rawPassword)
        val user = User(username = username, password = encodedPassword)
        return userRepository.save(user)
    }

    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }
}
