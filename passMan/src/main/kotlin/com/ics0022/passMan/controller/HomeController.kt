package com.ics0022.passMan.controller

import com.ics0022.passMan.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController(
    private val userRepository: UserRepository
) {

    @GetMapping("/home")
    fun showVaultsForm(model: Model): String {
        val auth = SecurityContextHolder.getContext().authentication
        val username = auth.name
        val user = userRepository.findByUsername(username) ?: throw RuntimeException("User not found")

        val vaults = user.vaults
        model.addAttribute("vaults", vaults)

        return "home"
    }
}
