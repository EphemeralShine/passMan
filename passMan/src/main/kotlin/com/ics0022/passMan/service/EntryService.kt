package com.ics0022.passMan.service

import com.ics0022.passMan.model.User
import com.ics0022.passMan.model.Entry
import com.ics0022.passMan.repository.EntryRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class EntryService(
    private val entryRepository: EntryRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun isEntryNameExists(entryName: String): Boolean {
        return entryRepository.findByName(entryName) != null
    }

    fun createEntry(name: String, rawPassword: String, user: User): Entry {
        val encodedPassword = passwordEncoder.encode(rawPassword)
        val entry = Entry(name = name, password = encodedPassword, user = user)
        return entryRepository.save(entry)
    }

    fun getEntryById(id: UUID): Entry? {
        return entryRepository.findById(id).getOrNull()
    }

}
