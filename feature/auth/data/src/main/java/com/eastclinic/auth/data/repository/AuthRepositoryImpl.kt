package com.eastclinic.auth.data.repository

import com.eastclinic.auth.domain.model.User
import com.eastclinic.auth.domain.repository.AuthRepository
import com.eastclinic.core.common.Result
import javax.inject.Inject

/**
 * Stub implementation of AuthRepository.
 */
class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    override suspend fun login(username: String, password: String): Result<User> {
        // Stub implementation
        return Result.Success(User(id = "1", name = username))
    }
    
    override suspend fun logout(): Result<Unit> {
        // Stub implementation
        return Result.Success(Unit)
    }
}


