package com.ics0022.passMan

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import com.ics0022.passMan.config.DotenvInitializer

@SpringBootApplication
class PassManApplication

fun main(args: Array<String>) {
	val app = SpringApplicationBuilder(PassManApplication::class.java)
		.initializers(DotenvInitializer())
		.run(*args)

}
