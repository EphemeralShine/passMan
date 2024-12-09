package com.ics0022.passMan.service

import com.ics0022.passMan.model.Password
import com.ics0022.passMan.repository.VaultRepository
import com.ics0022.passMan.repository.PasswordRepository
import com.ics0022.passMan.util.EncryptionUtil
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class PasswordService(
    private val passwordRepository: PasswordRepository,
    private val vaultRepository: VaultRepository
) {

    fun createPassword(name: String, rawPassword: String, vaultId: UUID, session: HttpSession): Password {
        val vault = vaultRepository.findById(vaultId).getOrNull() ?: throw Exception("Vault not found")
        val encryptionKey = session.getAttribute(vault.name) as? String
        val encryptionKeyBytes = Base64.getDecoder().decode(encryptionKey)
        val encryptionUtil = EncryptionUtil()
        val encryptedPassword = encryptionUtil.encryptData(rawPassword, encryptionKeyBytes)
        val password = Password(name = name, password = Base64.getEncoder().encodeToString(encryptedPassword), vault = vault)
        return passwordRepository.save(password)
    }

    fun getPasswordById(id: UUID): Password? {
        return passwordRepository.findById(id).getOrNull()
    }
}