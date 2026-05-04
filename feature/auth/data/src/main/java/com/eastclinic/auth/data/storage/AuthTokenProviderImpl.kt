package com.eastclinic.auth.data.storage

import com.eastclinic.core.auth.AuthTokenProvider
import com.eastclinic.core.auth.SessionStore
import com.eastclinic.core.common.Result as AppResult
import com.eastclinic.core.common.getOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenProviderImpl @Inject constructor(
    private val sessionStore: SessionStore
) : AuthTokenProvider {

    override suspend fun getToken(): String? {
        return sessionStore.getSession().getOrNull()?.token
    }

    override suspend fun refreshToken(): AppResult<String> {
        // Refresh logic can be implemented here using AuthApi
        return AppResult.Error(com.eastclinic.core.common.AppError.AuthError("Refresh not implemented"))
    }
}
