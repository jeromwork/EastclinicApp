package com.eastclinic.auth.domain.model

import com.eastclinic.core.common.Result

/**
 * Stub domain model for demonstration of using Result<T>.
 */
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val phone: String,
    val email: String,
    val status: UserStatus
)

enum class UserStatus {
    UNCONFIRMED,
    CONFIRMED,
    BLOCKED
}



