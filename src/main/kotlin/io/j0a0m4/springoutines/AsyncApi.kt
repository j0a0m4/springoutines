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
		.also {
			CoroutineScope(asyncDispatcher).launch {
				val deferred = async { delayedProcess() }
				tracker.track(it, deferred)
			}
		}.let {
			TaskStatus(it, CoroutineStatus.Processing)
		}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	suspend fun get() = tracker.retrieve()
		.map { (id, deferred) ->
			TaskStatus.from(id, deferred)
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
