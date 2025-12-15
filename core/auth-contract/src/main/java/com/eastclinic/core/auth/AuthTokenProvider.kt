package com.eastclinic.core.auth

import com.eastclinic.core.common.Result

/**
 * Interface for providing authorization token.
 */
interface AuthTokenProvider {
    suspend fun getToken(): String?
    suspend fun refreshToken(): Result<String>
}
