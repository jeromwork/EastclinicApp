package com.eastclinic.core.common

import org.junit.Assert.*
import org.junit.Test

class AppErrorTest {
    @Test
    fun `NetworkError has message and code`() {
        val error = AppError.NetworkError("Network error", 500)
        assertEquals("Network error", error.message)
        assertEquals(500, error.code)
    }
    
    @Test
    fun `ValidationError has field and message`() {
        val error = AppError.ValidationError("email", "Invalid email")
        assertEquals("email", error.field)
        assertEquals("Invalid email", error.message)
    }
    
    @Test
    fun `ValidationError can have null field`() {
        val error = AppError.ValidationError(null, "Validation failed")
        assertNull(error.field)
        assertEquals("Validation failed", error.message)
    }
    
    @Test
    fun `UnknownError has message and cause`() {
        val cause = RuntimeException("Root cause")
        val error = AppError.UnknownError("Unknown error", cause)
        assertEquals("Unknown error", error.message)
        assertEquals(cause, error.cause)
    }
    
    @Test
    fun `UnknownError can have null cause`() {
        val error = AppError.UnknownError("Unknown error", null)
        assertEquals("Unknown error", error.message)
        assertNull(error.cause)
    }
}
