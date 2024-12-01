package com.ics0022.passMan.service

import com.ics0022.passMan.model.User
import com.ics0022.passMan.model.Entry
import com.ics0022.passMan.repository.EntryRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class EntryService(
    private val entryRepository: EntryRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun isEntryNameExists(entryName: String): Boolean {
        return entryRepository.findByName(entryName) != null
    }

    fun createEntry(name: String, rawPassword: String, user: User): Entry {
        val entry = Entry(name = name, password = rawPassword, user = user)
        return entryRepository.save(entry)
    }

}
