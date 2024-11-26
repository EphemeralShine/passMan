package com.ics0022.passMan.service
import com.ics0022.passMan.repository.UserRepository
import com.ics0022.passMan.model.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder) {

    fun save(user: User) {
        user.password = passwordEncoder.encode(user.password)
        userRepository.save(user)
    }
}