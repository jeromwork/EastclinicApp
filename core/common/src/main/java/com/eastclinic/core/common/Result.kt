package com.eastclinic.core.common

/**
 * Sealed class for type-safe representation of operation results.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: AppError) : Result<Nothing>()
}


