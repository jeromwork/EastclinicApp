package com.eastclinic.core.common

/**
 * Test utilities for creating AppError instances.
 */
object AppErrorFactory {
    fun networkError(message: String = "Network error", code: Int? = null): AppError =
        AppError.NetworkError(message, code)
    
    fun validationError(field: String? = null, message: String = "Validation error"): AppError =
        AppError.ValidationError(field, message)
    
    fun unknownError(message: String = "Unknown error", cause: Throwable? = null): AppError =
        AppError.UnknownError(message, cause)
}
