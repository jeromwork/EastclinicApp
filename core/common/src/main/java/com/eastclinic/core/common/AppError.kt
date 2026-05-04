package com.eastclinic.core.common

/**
 * Sealed class for normalized error representation across all application layers.
 */
sealed class AppError {
    abstract val message: String
    
    data class NetworkError(
        override val message: String,
        val code: Int? = null
    ) : AppError()
    
    data class ValidationError(
        val field: String? = null,
        override val message: String
    ) : AppError()
    
    data class UnknownError(
        override val message: String,
        val cause: Throwable? = null
    ) : AppError()
    
    data class AuthError(
        override val message: String,
        val type: AuthErrorType = AuthErrorType.UNKNOWN
    ) : AppError()
    
    enum class AuthErrorType {
        GOOGLE_CANCELLED,
        GOOGLE_SIGN_IN_FAILED,
        INVALID_TOKEN,
        UNAUTHORIZED,
        USER_BLOCKED,
        UNKNOWN
    }
}



