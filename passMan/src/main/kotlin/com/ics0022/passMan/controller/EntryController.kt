package com.ics0022.passMan.controller

import com.ics0022.passMan.repository.UserRepository
import com.ics0022.passMan.service.EntryService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class EntryController(
    private val userRepository: UserRepository,
    private val entryService: EntryService
) {

    @PostMapping("/home/createEntry")
    fun createVault(@RequestParam vaultName: String, @RequestParam masterPassword: String, model: Model): String {
        val auth = SecurityContextHolder.getContext().authentication
        val username = auth.name
        val user = userRepository.findByUsername(username) ?: throw RuntimeException("User not found")

        try{
        entryService.createEntry(name = vaultName, rawPassword = masterPassword, user = user)
        model.addAttribute("success", "Vault created successfully!")
            return "redirect:/home"
        } catch (e: Exception) {
            model.addAttribute("error", "Something went wrong")
            return "redirect:/home"
        }

    }
}
