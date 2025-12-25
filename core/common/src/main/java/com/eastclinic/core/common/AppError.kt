package com.eastclinic.core.common

/**
 * Sealed class for normalized error representation across all application layers.
 * 
 * Разделение технической причины (message) и UI-сообщения (userMessage):
 * - message: техническая информация для логирования/отладки
 * - userMessage: локализуемое сообщение для пользователя
 */
sealed class AppError {
    /**
     * Техническое сообщение для логирования и отладки.
     * Может содержать технические детали, не предназначенные для пользователя.
     */
    abstract val message: String
    
    /**
     * Сообщение для отображения пользователю.
     * Должно быть локализуемым и понятным.
     * По умолчанию равен message, но может быть переопределен.
     */
    open val userMessage: String
        get() = message
    
    data class NetworkError(
        override val message: String,
        val code: Int? = null,
        override val userMessage: String = message
    ) : AppError()
    
    data class ValidationError(
        val field: String? = null,
        override val message: String,
        override val userMessage: String = message
    ) : AppError()
    
    data class UnknownError(
        override val message: String,
        val cause: Throwable? = null,
        override val userMessage: String = message
    ) : AppError()
}


