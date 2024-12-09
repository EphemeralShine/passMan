package com.ics0022.passMan.model

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "vaults")
data class Vault (
    @Id
    @Column(nullable = false, unique = true)
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    val name: String = "",
    @Column(nullable = false)
    val password: String = "",
    @Column(nullable = false)
    val salt: ByteArray,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    @OneToMany(mappedBy = "vault", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val passwords: List<Password> = mutableListOf()
){
    override fun toString(): String {
        return "Entry(name=$name, password=$password)"
    }
}