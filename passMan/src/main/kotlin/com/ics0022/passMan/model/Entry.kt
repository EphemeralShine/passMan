package com.ics0022.passMan.model

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "entries")
data class Entry (
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false, unique = true)
    val name: String = "",
    @Column(nullable = false)
    val password: String = "",
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
){
    override fun toString(): String {
        return "Entry(name=$name, password=$password)"
    }
}