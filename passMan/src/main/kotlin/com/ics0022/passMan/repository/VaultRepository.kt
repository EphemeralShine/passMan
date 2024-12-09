package com.ics0022.passMan.repository

import com.ics0022.passMan.model.Vault
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface VaultRepository : JpaRepository<Vault, UUID> {
    fun findByName(name: String): Vault?
}