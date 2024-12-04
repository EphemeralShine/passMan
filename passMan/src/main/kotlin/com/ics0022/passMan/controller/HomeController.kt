package com.ics0022.passMan.controller

import com.ics0022.passMan.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class HomeController(
    private val userRepository: UserRepository
) {

    @GetMapping("/home")
    fun showVaultsForm(model: Model, redirectAttributes: RedirectAttributes): String {
        val auth = SecurityContextHolder.getContext().authentication
        val username = auth.name
        val user = userRepository.findByUsername(username) ?: throw RuntimeException("User not found")

        val vaults = user.vaults
        redirectAttributes.addFlashAttribute("vaults", vaults)

        return "home"
    }
}
