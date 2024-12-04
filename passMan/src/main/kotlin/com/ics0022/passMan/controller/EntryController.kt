package com.ics0022.passMan.controller
import com.ics0022.passMan.model.Entry
import com.ics0022.passMan.repository.UserRepository
import com.ics0022.passMan.service.EntryService
import com.ics0022.passMan.util.KDFutil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*

@Controller
class EntryController(
    private val userRepository: UserRepository,
    private val entryService: EntryService,
    private val passwordEncoder: PasswordEncoder,
) {

    @PostMapping("/home/createEntry")
    fun createVault(
        @RequestParam vaultName: String,
        @RequestParam masterPassword: String,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        val auth = SecurityContextHolder.getContext().authentication
        val username = auth.name
        val user = userRepository.findByUsername(username) ?: throw RuntimeException("User not found")

        try{
        entryService.createEntry(name = vaultName, rawPassword = masterPassword, user = user)
            redirectAttributes.addFlashAttribute("success", "Vault created successfully!")
            return "redirect:/home"
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong")
            return "redirect:/home"
        }

    }

    @PostMapping("/home")
    fun handleVaultPassword(
        @RequestParam vaultId: UUID,
        @RequestParam vaultPassword: String,
        model: Model,
        request: HttpServletRequest,
        redirectAttributes: RedirectAttributes
    ): String {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val loggedInUser = authentication.name
        val vault: Entry? = entryService.getEntryById(vaultId)

        if (vault != null && vault.user.username == loggedInUser) {
            if (passwordEncoder.matches(vaultPassword, vault.password)) {
                val kdfUtil = KDFutil()
                val session = request.session
                val encryptionKey = kdfUtil.deriveEncryptionKey(vaultPassword, vault.salt)

                session.setAttribute(vault.name, encryptionKey)
            return "redirect:/vaultDashboard/${vaultId}"
        } else {
                redirectAttributes.addFlashAttribute("error", "Invalid password!")
            return "redirect:/home"
        }
        } else {
            redirectAttributes.addFlashAttribute("error", "Vault not found!")
            return "redirect:/home"
        }

    }
}
