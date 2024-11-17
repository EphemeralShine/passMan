package com.ics0022.passMan.model

import jakarta.persistence.*

@Entity
data class PasswordEntry(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
    val website: String,
    val username: String,
    val password: String, // Consider encrypting this field
    val userId: Long // To associate entries with users
)