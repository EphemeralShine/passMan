package com.ics0022.passMan.controller

import com.ics0022.passMan.model.User
import com.ics0022.passMan.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class RegistrationController(
    private val userService: UserService
) {

    @GetMapping("/register")
    fun showRegistrationForm(model: Model): String {
        model.addAttribute("user", User())
        return "register"
    }

    @PostMapping("/register")
    fun register(
        @RequestParam username: String,
        @RequestParam password: String,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        try {
            userService.registerUser(username, password)
            redirectAttributes.addFlashAttribute("success", "User registered successfully!")
            return "redirect:/login"
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "Username already in use")
            return "redirect:/register"
        }
    }
}
