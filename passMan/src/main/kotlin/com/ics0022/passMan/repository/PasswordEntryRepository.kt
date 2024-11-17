package com.ics0022.passMan.repository

import com.ics0022.passMan.model.PasswordEntry
import org.springframework.data.jpa.repository.JpaRepository

interface PasswordEntryRepository : JpaRepository<PasswordEntry, Long> {
    fun findByUserId(userId: Long): List<PasswordEntry>
}