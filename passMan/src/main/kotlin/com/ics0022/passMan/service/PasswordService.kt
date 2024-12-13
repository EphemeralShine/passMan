package com.ics0022.passMan.service

import com.ics0022.passMan.model.Password
import com.ics0022.passMan.repository.VaultRepository
import com.ics0022.passMan.repository.PasswordRepository
import com.ics0022.passMan.repository.UserRepository
import com.ics0022.passMan.util.EncryptionUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class PasswordService(
    private val passwordRepository: PasswordRepository,
    private val vaultRepository: VaultRepository,
    private val userRepository: UserRepository
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

    fun deleteVaultPassword(vaultId: UUID, passwordId: UUID, username: String, session: HttpSession) {
        val vault = vaultRepository.findById(vaultId).orElseThrow { VaultNotFoundException("Vault not found") }

        if (vault.user.username != username) {
            throw AccessDeniedException("User is not authorized to access this vault")
        }

        val password = passwordRepository.findById(passwordId).orElseThrow { PasswordNotFoundException("Password not found") }

        val encryptionKey = session.getAttribute(vault.name) as? String
            ?: throw EncryptionKeyNotFoundException("Encryption key not found in session")

        passwordRepository.delete(password)
    }

    fun createPassword(
        username: String,
        vaultId: UUID,
        request: HttpServletRequest,
        name: String,
        password: String
    ) {
        val vault = vaultRepository.findById(vaultId).orElseThrow { VaultNotFoundException("Vault not found") }
        val encryptionKey = request.session.getAttribute(vault.name) as? String
            ?: throw EncryptionKeyNotFoundException("Encryption key not found in session")

        try {
            createPassword(
                name = name,
                rawPassword = password,
                vaultId = vaultId,
                session = request.session
            )
        } catch (e: Exception) {
            throw  VaultNotFoundException("Vault not found")
        }
    }

}