package io.j0a0m4.springoutines

import kotlinx.coroutines.Deferred
import java.util.*

data class TaskStatus(val taskId: UUID, val status: CoroutineStatus) {
	companion object {
		suspend fun <T> from(taskId: UUID, deferred: Deferred<Result<T>>) =
			TaskStatus(taskId, CoroutineStatus.from(deferred))
	}
}