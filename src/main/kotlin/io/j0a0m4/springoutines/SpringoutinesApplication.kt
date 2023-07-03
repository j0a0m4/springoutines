package io.j0a0m4.springoutines

import kotlinx.coroutines.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import kotlin.time.Duration.Companion.seconds

@SpringBootApplication
class SpringoutinesApplication {
	@Bean
	fun coroutineDispatcher(): CoroutineDispatcher =
		Dispatchers.IO
}

fun main(args: Array<String>) {
	runApplication<SpringoutinesApplication>(*args)
}

suspend fun delayedProcess() = runCatching {
	delay((5..10).random().seconds)
	listOf("Boom!", "Wow!", "Yay!").run {
		shuffled().first()
	}.also {
		if (it == "Boom!") {
			throw RuntimeException("$it I bet you didn't see that coming")
		}
	}
}