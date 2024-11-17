package com.ics0022.passMan.service

import com.ics0022.passMan.model.PasswordEntry
import com.ics0022.passMan.repository.PasswordEntryRepository
import org.springframework.stereotype.Service

@Service
class PasswordManagerService(private val repository: PasswordEntryRepository) {
    fun addEntry(entry: PasswordEntry): PasswordEntry = repository.save(entry)
    fun getEntriesByUser(userId: Long): List<PasswordEntry> = repository.findByUserId(userId)
    fun deleteEntry(id: Long) = repository.deleteById(id)
}