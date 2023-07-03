package io.j0a0m4.springoutines

import kotlinx.coroutines.Deferred
import org.springframework.stereotype.Component
import java.util.*

@Component
class Tracker<T> : AsyncTracker<T> {
	val state = mutableMapOf<UUID, Deferred<T>>()

	override fun track(id: UUID, deferred: Deferred<T>) {
		state[id] = deferred
	}

	override fun clear() {
		state.clear()
	}

	override fun retrieve(): Map<UUID, Deferred<T>> =
		state.toMap()

	override fun get(id: UUID) =
		state[id]
}