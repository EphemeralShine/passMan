package com.ics0022.passMan.controller

import com.ics0022.passMan.model.PasswordEntry
import com.ics0022.passMan.service.PasswordManagerService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/passwords")
class PasswordManagerController(private val service: PasswordManagerService) {

    @PostMapping
    fun addEntry(@RequestBody entry: PasswordEntry) = service.addEntry(entry)

    @GetMapping("/{userId}")
    fun getEntries(@PathVariable userId: Long) = service.getEntriesByUser(userId)

    @DeleteMapping("/{id}")
    fun deleteEntry(@PathVariable id: Long) = service.deleteEntry(id)
}