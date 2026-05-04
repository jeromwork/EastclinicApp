package com.eastclinic.core.common

/**
 * Sealed class for type-safe representation of operation results.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: AppError) : Result<Nothing>()
}

/**
 * Returns the encapsulated data if this instance represents [Result.Success],
 * or null if it is [Result.Error].
 */
fun <T> Result<T>.getOrNull(): T? {
    return when (this) {
        is Result.Success -> data
        is Result.Error -> null
    }
}



