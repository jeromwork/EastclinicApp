package com.eastclinic.core.async

import java.time.Instant

/**
 * Interface for time operations (for testability).
 */
interface Clock {
    fun now(): Instant
    fun nowMillis(): Long
}


