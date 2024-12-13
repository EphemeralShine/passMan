package com.ics0022.passMan.controller

import com.ics0022.passMan.model.Password
import com.ics0022.passMan.repository.PasswordRepository
import com.ics0022.passMan.repository.VaultRepository
import com.ics0022.passMan.repository.UserRepository
import com.ics0022.passMan.service.*
import com.ics0022.passMan.util.EncryptionUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Controller
class DashboardController(
    private val userRepository: UserRepository,
    private val passwordService: PasswordService,
    private val vaultRepository: VaultRepository,
    private val vaultService: VaultService
) {
    @GetMapping("/vaultDashboard/{vaultId}")
    fun showVault(
        @PathVariable vaultId: UUID,
        request: HttpServletRequest,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        val auth = SecurityContextHolder.getContext().authentication
        val username = auth.name

        return try {
            val (vault, decryptedPasswords) = vaultService.getDecryptedVaultPasswords(vaultId, username, request.session)

            model.addAttribute("vault", vault)
            model.addAttribute("decryptedPasswords", decryptedPasswords)
            "/vaultDashboard"
        } catch (e: VaultAccessException) {
            redirectAttributes.addFlashAttribute("error", e.message)
            "redirect:/home"
        } catch (e: EncryptionKeyNotFoundException) {
            redirectAttributes.addFlashAttribute("error", e.message)
            "redirect:/home"
        }
    }

    @PostMapping("/vaultDashboard/{vaultId}/createEntry")
    fun createPassword(
        @PathVariable vaultId: UUID,
        @RequestParam name: String,
        @RequestParam password: String,
        model: Model,
        request: HttpServletRequest,
        redirectAttributes: RedirectAttributes): String {
        val auth = SecurityContextHolder.getContext().authentication
        val username = auth.name
        try {
            passwordService.createPassword(username, vaultId, request, name, password)
        } catch (e: VaultNotFoundException) {
            redirectAttributes.addFlashAttribute("error", e.message)
            return "redirect:/home"
        } catch (e: EncryptionKeyNotFoundException) {
            redirectAttributes.addFlashAttribute("error", e.message)
            return "redirect:/home"
        }

        redirectAttributes.addFlashAttribute("success", "Password entry created successfully!")
        return "redirect:/vaultDashboard/${vaultId}"
    }


    @PostMapping("/vaultDashboard/{vaultId}/{passwordId}/delete")
        fun deleteVault(
            @PathVariable vaultId: UUID,
            @PathVariable passwordId: UUID,
            request: HttpServletRequest,
            redirectAttributes: RedirectAttributes
        ): String {
            val auth = SecurityContextHolder.getContext().authentication
            val username = auth.name

            return try {
                passwordService.deleteVaultPassword(vaultId, passwordId, username, request.session)

                "redirect:/vaultDashboard/$vaultId"
            } catch (e: VaultNotFoundException) {
                redirectAttributes.addFlashAttribute("error", e.message)
                "redirect:/home"
            } catch (e: PasswordNotFoundException) {
                redirectAttributes.addFlashAttribute("error", e.message)
                "redirect:/home"
            } catch (e: EncryptionKeyNotFoundException) {
                redirectAttributes.addFlashAttribute("error", e.message)
                "redirect:/home"
            } catch (e: AccessDeniedException) {
                redirectAttributes.addFlashAttribute("error", e.message)
                "redirect:/home"
            }
        }
    }
