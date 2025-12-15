package com.eastclinic.core.network

import com.eastclinic.core.common.AppError

/**
 * Extension function to map NetworkError to AppError.
 */
fun NetworkError.toAppError(): AppError {
    return AppError.NetworkError(
        message = this.message,
        code = this.code
    )
}
