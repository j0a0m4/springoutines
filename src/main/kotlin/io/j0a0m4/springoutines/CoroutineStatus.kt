package io.j0a0m4.springoutines

import kotlinx.coroutines.Deferred

enum class CoroutineStatus {
	Processing, Success, Failure;

	companion object {
		suspend fun <T> from(deferred: Deferred<Result<T>>): CoroutineStatus =
			if (deferred.isActive) Processing
			else deferred.await().fold(
				onSuccess = { Success },
				onFailure = { Failure }
			)
	}
}