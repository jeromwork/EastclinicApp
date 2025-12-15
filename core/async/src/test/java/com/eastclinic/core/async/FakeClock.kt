package com.eastclinic.core.async

import java.time.Instant

/**
 * Fake implementation of Clock for testing.
 */
class FakeClock(
    private var currentTime: Instant = Instant.now()
) : Clock {
    override fun now(): Instant = currentTime
    
    override fun nowMillis(): Long = currentTime.toEpochMilli()
    
    fun advanceTime(seconds: Long) {
        currentTime = currentTime.plusSeconds(seconds)
    }
    
    fun setTime(instant: Instant) {
        currentTime = instant
    }
}
