package com.ics0022.passMan.repository

import com.ics0022.passMan.model.Password
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PasswordRepository : JpaRepository<Password, UUID> {
    fun findByName(name: String): Password?
}