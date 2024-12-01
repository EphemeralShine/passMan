package com.ics0022.passMan.controller

import com.ics0022.passMan.model.User
import com.ics0022.passMan.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

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
        model: Model
    ): String {
        try {
            userService.registerUser(username, password)
            return "redirect:/login"
        } catch (e: Exception) {
            model.addAttribute("error", "User already exists")
            return "register"
        }
    }
}
