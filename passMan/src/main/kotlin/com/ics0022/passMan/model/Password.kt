package com.ics0022.passMan.model

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "passwords")
data class Password (
    @Id
    @Column(nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    val name: String = "",
    @Column(nullable = false)
    val password: String = "",
    @ManyToOne
    @JoinColumn(name = "vault_id", nullable = false)
    val vault: Vault
){
    override fun toString(): String {
        return "Entry(name=$name, password=$password)"
    }
}