package com.ics0022.passMan.config

import org.springframework.boot.web.server.ConfigurableWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter

@Configuration
class SecurityConfig(
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity, configurableWebServerFactory: ConfigurableWebServerFactory): SecurityFilterChain {
        http
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/public/**", "/register", "/login").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { login ->
                login
                    .loginPage("/login")
                    .defaultSuccessUrl("/home", true)
                    .permitAll()
            }
            .logout { logout ->
                logout.logoutSuccessUrl("/login")
                logout.permitAll()
            }
            .sessionManagement { session ->
                session
                    .maximumSessions(1)
                    .expiredUrl("/login?expired=true")
            }
            .csrf { csrf ->
                csrf
                    .csrfTokenRepository(HttpSessionCsrfTokenRepository())
            }
            .headers { headers ->
                headers
                    .xssProtection { xss ->
                        xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                    }
                    .contentSecurityPolicy { csp ->
                        csp.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self'; connect-src 'self'; font-src 'self'; frame-ancestors 'none';")
                    }
            }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }


    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }
}
