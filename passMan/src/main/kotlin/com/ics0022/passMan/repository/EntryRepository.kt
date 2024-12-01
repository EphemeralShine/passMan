package com.ics0022.passMan.repository

import com.ics0022.passMan.model.Entry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EntryRepository : JpaRepository<Entry, Long> {
    fun findByName(name: String): Entry?
}