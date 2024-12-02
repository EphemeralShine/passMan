package com.ics0022.passMan.config

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

class DotenvInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val dotenv = Dotenv.configure()
            .directory(System.getProperty("user.dir"))
            .filename(".env")
            .load()

        dotenv.entries().forEach { entry ->
            System.setProperty(entry.key, entry.value)
        }
    }
}
