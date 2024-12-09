package com.ics0022.passMan.controller

import com.ics0022.passMan.model.Password
import com.ics0022.passMan.repository.PasswordRepository
import com.ics0022.passMan.repository.VaultRepository
import com.ics0022.passMan.repository.UserRepository
import com.ics0022.passMan.service.PasswordService
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
    private val passwordRepository: PasswordRepository
) {
    @GetMapping("/vaultDashboard/{vaultId}")
    fun showVault(
        @PathVariable vaultId: UUID,
        request: HttpServletRequest,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        val auth = SecurityContextHolder.getContext().authentication
        val username = auth.name
        val user = userRepository.findByUsername(username) ?: return "errorPage"

        val vault = user.vaults.find { it.id == vaultId }
            ?: return "errorPage"

        val session = request.session
        val encryptionKey = session.getAttribute(vault.name) as? String
            ?: return "errorPage"

        val encPasswords = vault.passwords
        val decryptedPasswords = mutableListOf<Password>()
        val encryptionUtil = EncryptionUtil()
            for (encPassword in encPasswords) {
                val encryptedPassword = Base64.getDecoder().decode(encPassword.password)
                val encryptionKeyBytes = Base64.getDecoder().decode(encryptionKey)
                decryptedPasswords.add(encPassword.copy(password = encryptionUtil.decryptData(encryptedPassword, encryptionKeyBytes)))
            }
        model.addAttribute("vault", vault)
        model.addAttribute("decryptedPasswords", decryptedPasswords)
        return "/vaultDashboard"
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
        val user = userRepository.findByUsername(username) ?: return "errorPage"
        val vault = vaultRepository.findById(vaultId).getOrNull() ?: throw Exception("Vault not found")
        val encryptionKey = request.session.getAttribute(vault.name) as? String
            ?: return "errorPage"

        try{
            passwordService.createPassword(name = name, rawPassword = password, vaultId = vaultId, session = request.session)
            } catch (e: Exception) {
                return "errorPage"
            }
        redirectAttributes.addFlashAttribute("success", "Password entry created successfully!")
        return "redirect:/vaultDashboard/${vaultId}"
    }

    @PostMapping("/vaultDashboard/{vaultId}/{passwordId}/delete")
    fun deleteVault(@PathVariable vaultId: UUID, @PathVariable passwordId: UUID, request: HttpServletRequest, model: Model): String {
        val auth = SecurityContextHolder.getContext().authentication
        val username = auth.name

        val vault = vaultRepository.findById(vaultId).orElse(null) ?: return "errorPage"
        val password = passwordRepository.findById(passwordId).orElse(null) ?: return "errorPage"
        val encryptionKey = request.session.getAttribute(vault.name) as? String
            ?: return "errorPage"

        if (vault.user.username != username) {
            return "errorPage"
        }

        passwordRepository.delete(password)
        return "redirect:/vaultDashboard/${vaultId}"
    }
}