package com.ics0022.passMan.controller

import com.ics0022.passMan.service.VaultService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class HomeController(
    private val vaultService: VaultService
) {

    @GetMapping("/home")
    fun showVaultsForm(model: Model, redirectAttributes: RedirectAttributes): String {
        val auth = SecurityContextHolder.getContext().authentication
        val username = auth.name
        val vaults = vaultService.getUserVaults(username)
        model.addAttribute("vaults", vaults)

        return "home"
    }

}
