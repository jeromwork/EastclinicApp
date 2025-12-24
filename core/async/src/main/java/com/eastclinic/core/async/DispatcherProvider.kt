package com.eastclinic.core.async

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Interface for providing coroutine dispatchers (for testability).
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}


