package com.eastclinic.core.auth

import com.eastclinic.core.common.Result

/**
 * Interface for session storage.
 */
interface SessionStore {
    suspend fun saveSession(session: Session): Result<Unit>
    suspend fun getSession(): Result<Session?>
    suspend fun clearSession(): Result<Unit>
}

/**
 * Session data model.
 */
data class Session(
    val userId: String,
    val token: String,
    val refreshToken: String? = null
)


