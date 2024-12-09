package com.ics0022.passMan.service

import com.ics0022.passMan.model.User
import com.ics0022.passMan.model.Vault
import com.ics0022.passMan.repository.VaultRepository
import com.ics0022.passMan.util.KDFutil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class VaultService(
    private val vaultRepository: VaultRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun isEntryNameExists(entryName: String): Boolean {
        return vaultRepository.findByName(entryName) != null
    }

    fun createEntry(name: String, rawPassword: String, user: User): Vault {
        val encodedPassword = passwordEncoder.encode(rawPassword)
        val kdfUtil = KDFutil()
        val salt = kdfUtil.generateSalt()
        val entry = Vault(name = name, password = encodedPassword, user = user, salt = salt)
        return vaultRepository.save(entry)
    }

    fun getEntryById(id: UUID): Vault? {
        return vaultRepository.findById(id).getOrNull()
    }

}
