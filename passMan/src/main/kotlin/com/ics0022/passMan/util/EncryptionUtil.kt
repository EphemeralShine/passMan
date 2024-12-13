package com.ics0022.passMan.util

import org.springframework.stereotype.Component
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class EncryptionUtil {
    private val GCM_NONCE_LENGTH = 12 // Recommended length for GCM nonce
    private val GCM_TAG_LENGTH = 128 // Authentication tag length (in bits)

    fun encryptData(data: String, key: ByteArray): ByteArray {
        val nonce = ByteArray(GCM_NONCE_LENGTH)
        val random = java.security.SecureRandom()
        random.nextBytes(nonce)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey: SecretKey = SecretKeySpec(key, "AES")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, nonce)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

        val encryptedData = cipher.doFinal(data.toByteArray())

        return nonce + encryptedData
    }

    fun decryptData(encryptedData: ByteArray, key: ByteArray): String {
        val nonce = encryptedData.copyOfRange(0, GCM_NONCE_LENGTH)

        val cipherText = encryptedData.copyOfRange(GCM_NONCE_LENGTH, encryptedData.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey: SecretKey = SecretKeySpec(key, "AES")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, nonce)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

        val decryptedData = cipher.doFinal(cipherText)

        return String(decryptedData)
    }
}