package com.ics0022.passMan.controller
import com.ics0022.passMan.model.Entry
import com.ics0022.passMan.model.User
import com.ics0022.passMan.repository.UserRepository
import com.ics0022.passMan.service.EntryService
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.security.Principal
import java.util.*

@Controller
class EntryController(
    private val userRepository: UserRepository,
    private val entryService: EntryService,
    private val passwordEncoder: PasswordEncoder
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

    @PostMapping("/home")
    fun handleVaultPassword(
        @RequestParam vaultId: UUID,
        @RequestParam vaultPassword: String,
        model: Model
    ): String {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val loggedInUser = authentication.principal as org.springframework.security.core.userdetails.User
        val vault: Entry? = entryService.getEntryById(vaultId)

        if (vault != null && vault.user.username == loggedInUser.username) {
            if (passwordEncoder.matches(vaultPassword, vault.password)) {
            return "redirect:/vaultDashboard"
        } else {
            model.addAttribute("error", "Invalid password!")
            return "redirect:/home"
        }
        } else {
            model.addAttribute("error", "Vault not found!")
            return "redirect:/home"
        }

    }
}
