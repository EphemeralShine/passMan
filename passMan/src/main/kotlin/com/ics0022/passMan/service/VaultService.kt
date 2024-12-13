package com.ics0022.passMan.service

import com.ics0022.passMan.model.Password
import com.ics0022.passMan.model.User
import com.ics0022.passMan.model.Vault
import com.ics0022.passMan.repository.PasswordRepository
import com.ics0022.passMan.repository.UserRepository
import com.ics0022.passMan.repository.VaultRepository
import com.ics0022.passMan.util.EncryptionUtil
import com.ics0022.passMan.util.KDFutil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class VaultService(
    private val vaultRepository: VaultRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
    private val encryptionUtil: EncryptionUtil,
    private val passwordRepository: PasswordRepository
) {
    class InvalidInputException(message: String) : RuntimeException(message)
    fun isEntryNameExists(entryName: String): Boolean {
        return vaultRepository.findByName(entryName) != null
    }
     fun deleteEntry(
        vaultId: UUID,
        request: HttpServletRequest,
        username: String?
    ) {
        val vault = vaultRepository.findById(vaultId).orElse(null) ?: throw RuntimeException("Vault not found")
        val encryptionKey = request.session.getAttribute(vault.name) as? String
            ?: throw RuntimeException("Encryption key not found")

        if (vault.user.username != username) {
            throw RuntimeException("User not authorized to delete this vault")
        }

        vaultRepository.delete(vault)
    }

    fun createEntry(name: String, rawPassword: String, user: User): Vault {
        if (rawPassword.isEmpty()) {
            throw InvalidInputException("Password cannot be empty")
        }
        val encodedPassword = passwordEncoder.encode(rawPassword)
        val kdfUtil = KDFutil()
        val salt = kdfUtil.generateSalt()
        val entry = Vault(name = name, password = encodedPassword, user = user, salt = salt)
        return vaultRepository.save(entry)
    }

    fun getEntryById(id: UUID): Vault? {
        return vaultRepository.findById(id).getOrNull()
    }

    fun validateAndPrepareVault(vaultId: UUID, vaultPassword: String, loggedInUser: String): Pair<Vault, String> {
        val vault = vaultRepository.findById(vaultId).orElseThrow { VaultNotFoundException("Vault not found!") }

        if (vault.user.username != loggedInUser) {
            throw AccessDeniedException("User is not authorized to access this vault!")
        }

        if (!passwordEncoder.matches(vaultPassword, vault.password)) {
            throw InvalidPasswordException("Invalid password!")
        }

        val kdfUtil = KDFutil()
        val encryptionKey = kdfUtil.deriveEncryptionKey(vaultPassword, vault.salt)

        return Pair(vault, encryptionKey)
    }

    fun getUserVaults(username: String): List<Vault> {
        val user = userRepository.findByUsername(username) ?: throw RuntimeException("User not found")

        val vaults = user.vaults
        return vaults
    }

    fun getDecryptedVaultPasswords(vaultId: UUID, username: String, session: HttpSession): Pair<Vault, List<Password>> {
        val user = userRepository.findByUsername(username) ?: throw VaultAccessException("User not found")

        val vault = user.vaults.find { it.id == vaultId }
            ?: throw VaultAccessException("Vault not found for user")

        val encryptionKey = session.getAttribute(vault.name) as? String
            ?: throw EncryptionKeyNotFoundException("Encryption key not found in session")

        val decryptedPasswords = vault.passwords.map { encPassword ->
            val encryptedPassword = Base64.getDecoder().decode(encPassword.password)
            val encryptionKeyBytes = Base64.getDecoder().decode(encryptionKey)
            encPassword.copy(password = encryptionUtil.decryptData(encryptedPassword, encryptionKeyBytes))
        }

        return Pair(vault, decryptedPasswords)
    }

}
class VaultNotFoundException(message: String) : RuntimeException(message)
class InvalidPasswordException(message: String) : RuntimeException(message)
class AccessDeniedException(message: String) : RuntimeException(message)
class EncryptionKeyNotFoundException(message: String) : RuntimeException(message)
class VaultAccessException(message: String) : RuntimeException(message)
class PasswordNotFoundException(message: String) : RuntimeException(message)
