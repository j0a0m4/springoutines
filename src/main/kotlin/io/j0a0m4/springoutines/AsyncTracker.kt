package io.j0a0m4.springoutines

import kotlinx.coroutines.Deferred
import java.util.*

interface AsyncTracker<T> {
	fun track(id: UUID, deferred: Deferred<T>)
	fun clear()
	fun retrieve(): Map<UUID, Deferred<T>>
	operator fun get(id: UUID): Deferred<T>?
}