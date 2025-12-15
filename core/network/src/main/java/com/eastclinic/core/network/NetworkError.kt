package com.eastclinic.core.network

/**
 * Network error (maps to AppError.NetworkError).
 */
data class NetworkError(
    val message: String,
    val code: Int? = null,
    val cause: Throwable? = null
)
