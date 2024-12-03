package com.ics0022.passMan.model

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "passwords")
data class Password (
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false, unique = true)
    val name: String = "",
    @Column(nullable = false)
    val password: String = "",
    @ManyToOne
    @JoinColumn(name = "vault_id", nullable = false)
    val vault: Entry
){
    override fun toString(): String {
        return "Entry(name=$name, password=$password)"
    }
}