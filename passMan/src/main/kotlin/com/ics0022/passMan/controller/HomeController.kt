package com.ics0022.passMan.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

class HomeController {
    @Controller
    class HomeController {
        @GetMapping("/home")
        fun homePage(): String {
            return "home"
        }
    }
}