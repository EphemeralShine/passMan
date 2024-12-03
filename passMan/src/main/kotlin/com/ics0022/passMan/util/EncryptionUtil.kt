package com.ics0022.passMan.util

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptionUtil {
    private val GCM_NONCE_LENGTH = 12 // Recommended length for GCM nonce
    private val GCM_TAG_LENGTH = 128 // Authentication tag length (in bits)

    fun encryptData(data: String, key: ByteArray): ByteArray {
        // Generate a nonce (12 bytes)
        val nonce = ByteArray(GCM_NONCE_LENGTH)
        val random = java.security.SecureRandom()
        random.nextBytes(nonce)

        // Create the cipher and initialize it
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey: SecretKey = SecretKeySpec(key, "AES")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, nonce)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

        // Encrypt the data
        val encryptedData = cipher.doFinal(data.toByteArray())

        // Combine the nonce and encrypted data together (nonce + ciphertext)
        return nonce + encryptedData
    }

    fun decryptData(encryptedData: ByteArray, key: ByteArray): String {
        // Extract the nonce from the encrypted data (first 12 bytes)
        val nonce = encryptedData.copyOfRange(0, GCM_NONCE_LENGTH)

        // Extract the actual encrypted data (ciphertext)
        val cipherText = encryptedData.copyOfRange(GCM_NONCE_LENGTH, encryptedData.size)

        // Initialize the cipher with the same key and nonce used for encryption
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey: SecretKey = SecretKeySpec(key, "AES")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, nonce)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

        // Decrypt the data
        val decryptedData = cipher.doFinal(cipherText)

        return String(decryptedData)
    }
}