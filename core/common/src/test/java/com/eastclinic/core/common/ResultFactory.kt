package com.eastclinic.core.common

/**
 * Test utilities for creating Result instances.
 */
object ResultFactory {
    fun <T> success(data: T): Result<T> = Result.Success(data)
    
    fun <T> error(error: AppError): Result<T> = Result.Error(error)
    
    fun <T> networkError(message: String, code: Int? = null): Result<T> =
        Result.Error(AppError.NetworkError(message, code))
    
    fun <T> validationError(field: String? = null, message: String): Result<T> =
        Result.Error(AppError.ValidationError(field, message))
    
    fun <T> unknownError(message: String, cause: Throwable? = null): Result<T> =
        Result.Error(AppError.UnknownError(message, cause))
}
