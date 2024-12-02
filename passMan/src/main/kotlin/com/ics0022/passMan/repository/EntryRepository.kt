package com.ics0022.passMan.repository

import com.ics0022.passMan.model.Entry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface EntryRepository : JpaRepository<Entry, UUID> {
    fun findByName(name: String): Entry?
}