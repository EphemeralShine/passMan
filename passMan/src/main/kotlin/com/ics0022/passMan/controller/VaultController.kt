package com.ics0022.passMan.controller
import com.ics0022.passMan.model.Vault
import com.ics0022.passMan.repository.UserRepository
import com.ics0022.passMan.repository.VaultRepository
import com.ics0022.passMan.service.InvalidPasswordException
import com.ics0022.passMan.service.VaultNotFoundException
import com.ics0022.passMan.service.VaultService
import com.ics0022.passMan.util.KDFutil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*

@Controller
class VaultController(
    private val userRepository: UserRepository,
    private val vaultService: VaultService,
    private val passwordEncoder: PasswordEncoder,
    private val vaultRepository: VaultRepository
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
        vaultService.createEntry(name = vaultName, rawPassword = masterPassword, user = user)
            redirectAttributes.addFlashAttribute("success", "Vault created successfully!")
            return "redirect:/home"
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong")
            return "redirect:/home"
        } catch (e: VaultService.InvalidInputException) {
            redirectAttributes.addFlashAttribute("error", "Vault password cannot be empty")
            return "redirect:/home"
        }

    }

    @PostMapping("/home/{vaultId}/delete")
    fun deleteVault(@PathVariable vaultId: UUID, model: Model, request: HttpServletRequest,
                    redirectAttributes: RedirectAttributes
    ): String {
        val auth = SecurityContextHolder.getContext().authentication
        val username = auth.name
        try {
            vaultService.deleteEntry(vaultId, request, username)
        } catch (e: RuntimeException) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong")
            return "redirect:/home"
        }

        return "redirect:/home"
    }

    @Controller
    class VaultController(
        private val vaultService: VaultService
    ) {

        @PostMapping("/home")
        fun handleVaultPassword(
            @RequestParam vaultId: UUID,
            @RequestParam vaultPassword: String,
            model: Model,
            request: HttpServletRequest,
            redirectAttributes: RedirectAttributes
        ): String {
            return try {
                val authentication: Authentication = SecurityContextHolder.getContext().authentication
                val loggedInUser = authentication.name

                val (vault, encryptionKey) = vaultService.validateAndPrepareVault(vaultId, vaultPassword, loggedInUser)

                request.session.setAttribute(vault.name, encryptionKey)

                "redirect:/vaultDashboard/${vault.id}"
            } catch (e: VaultNotFoundException) {
                redirectAttributes.addFlashAttribute("error", e.message)
                "redirect:/home"
            } catch (e: InvalidPasswordException) {
                redirectAttributes.addFlashAttribute("error", e.message)
                "redirect:/home"
            } catch (e: AccessDeniedException) {
                redirectAttributes.addFlashAttribute("error", e.message)
                "redirect:/home"
            }
        }
    }
}
