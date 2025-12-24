package com.eastclinic.core.common

import org.junit.Assert.*
import org.junit.Test

class ResultTest {
    @Test
    fun `Success contains data`() {
        val result: Result<String> = Result.Success("test")
        assertTrue(result is Result.Success)
        assertEquals("test", (result as Result.Success).data)
    }
    
    @Test
    fun `Error contains AppError`() {
        val error = AppError.NetworkError("Network error", 500)
        val result: Result<String> = Result.Error(error)
        assertTrue(result is Result.Error)
        assertEquals(error, (result as Result.Error).error)
    }
}


