package com.eastclinic.core.network

import com.eastclinic.core.common.AppError
import org.junit.Assert.*
import org.junit.Test

class NetworkErrorMapperTest {
    @Test
    fun `NetworkError maps to AppError NetworkError`() {
        val networkError = NetworkError("Network error", 500)
        val appError = networkError.toAppError()
        
        assertTrue(appError is AppError.NetworkError)
        val networkAppError = appError as AppError.NetworkError
        assertEquals("Network error", networkAppError.message)
        assertEquals(500, networkAppError.code)
    }
    
    @Test
    fun `NetworkError with null code maps correctly`() {
        val networkError = NetworkError("Network error", null)
        val appError = networkError.toAppError()
        
        assertTrue(appError is AppError.NetworkError)
        val networkAppError = appError as AppError.NetworkError
        assertEquals("Network error", networkAppError.message)
        assertNull(networkAppError.code)
    }
}
