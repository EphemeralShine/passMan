package com.ics0022.passMan.util

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.util.Base64

class KDFutil {

    fun deriveEncryptionKey(password: String, salt: ByteArray): String {
        val keySpec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
        val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = keyFactory.generateSecret(keySpec).encoded
        return Base64.getEncoder().encodeToString(keyBytes)
    }

    fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }
}