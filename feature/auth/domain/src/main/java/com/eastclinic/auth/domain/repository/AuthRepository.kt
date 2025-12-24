package com.eastclinic.auth.domain.repository

import com.eastclinic.auth.domain.model.User
import com.eastclinic.core.common.Result

/**
 * Auth repository interface (stub for demonstration).
 */
interface AuthRepository {
    suspend fun login(username: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
}


