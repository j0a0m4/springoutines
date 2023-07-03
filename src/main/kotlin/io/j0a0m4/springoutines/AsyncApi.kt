package io.j0a0m4.springoutines

import kotlinx.coroutines.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api")
class AsyncApi(
	val asyncDispatcher: CoroutineDispatcher,
	val tracker: AsyncTracker<Result<String>>
) {
	@PostMapping
	@ResponseStatus(HttpStatus.ACCEPTED)
	suspend fun execute() = UUID.randomUUID()
		.also { id ->
			CoroutineScope(asyncDispatcher)
				.launch {
					async {
						delayedProcess()
					}.let {
						tracker.track(id, it)
					}
				}
		}.let {
			TaskStatus(it, CoroutineStatus.Processing)
		}


	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	suspend fun get() = tracker.retrieve()
		.map { (key, result) ->
			TaskStatus.from(key, result)
		}

	@GetMapping("/{id}")
	suspend fun getResult(@PathVariable id: UUID) = tracker[id]?.let {
		when (it.isActive) {
			true -> ResponseEntity.noContent().build()
			else -> it.await().fold(
				onSuccess = { s -> ResponseEntity.ok(s) },
				onFailure = { e -> ResponseEntity.ok(e.message) }
			)
		}
	} ?: ResponseEntity.notFound().build()
}
