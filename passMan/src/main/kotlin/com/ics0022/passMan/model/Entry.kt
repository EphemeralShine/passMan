package com.ics0022.passMan.model

import jakarta.persistence.*

@Entity
@Table(name = "entries")
data class Entry (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, unique = true)
    val name: String = "",
    @Column(nullable = false)
    val password: String = "",
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
)