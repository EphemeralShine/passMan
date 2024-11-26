package com.ics0022.passMan.controller
import com.ics0022.passMan.model.User
import com.ics0022.passMan.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class RegistrationController(private val userService: UserService) {

    @GetMapping("/register")
    fun showRegistrationForm(model: Model): String {
        model.addAttribute("user", User())
        return "register"
    }

    @PostMapping("/register")
    fun registerUser(user: User): String {
        userService.save(user)
        return "redirect:/login"
    }
}