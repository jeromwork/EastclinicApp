package com.eastclinic.auth.data.repository

import com.eastclinic.auth.data.api.AuthApi
import com.eastclinic.auth.data.api.dto.*
import com.eastclinic.auth.data.mapper.toDomain
import com.eastclinic.auth.data.mapper.toSessionStatus
import com.eastclinic.auth.domain.model.User
import com.eastclinic.auth.domain.model.UserProfile
import com.eastclinic.auth.domain.model.UserStatus
import com.eastclinic.auth.domain.model.VerificationSession
import com.eastclinic.auth.domain.repository.AuthRepository
import com.eastclinic.core.auth.Session
import com.eastclinic.core.auth.SessionStore
import com.eastclinic.core.common.Result as AppResult
import com.eastclinic.core.network.NetworkResult
import com.eastclinic.core.network.safeCall
import com.eastclinic.core.network.toAppError
import com.eastclinic.core.common.getOrNull
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val sessionStore: SessionStore
) : AuthRepository {

    override suspend fun login(username: String, password: String): AppResult<User> {
        // Legacy/Stub
        return AppResult.Success(User(
            id = "1", 
            firstName = username, 
            lastName = "", 
            middleName = null, 
            phone = "", 
            email = "", 
            status = UserStatus.CONFIRMED
        ))
    }

    override suspend fun handshakeSocial(provider: String, idToken: String): AppResult<User> {
        val request = SocialAuthRequestDto(
            provider = provider,
            idToken = idToken,
            device = getDeviceDto()
        )
        return handleAuthResponse { api.handshake(request) }
    }

    override suspend fun bootstrapSocial(
        provider: String,
        idToken: String,
        profile: UserProfile
    ): AppResult<User> {
        val request = SocialBootstrapRequestDto(
            provider = provider,
            idToken = idToken,
            firstName = profile.firstName,
            lastName = profile.lastName,
            middleName = profile.middleName,
            phone = profile.phone,
            device = getDeviceDto()
        )
        return handleAuthResponse { api.bootstrap(request) }
    }

    override suspend fun logout(): AppResult<Unit> {
        sessionStore.clearSession()
        return safeCall { api.logout() }.toAppResult { Unit }
    }

    override suspend fun createVerificationSession(): AppResult<VerificationSession> {
        return safeCall { api.createVerificationSession() }.toAppResult { it.toDomain() }
    }

    override suspend fun checkVerificationStatus(sessionId: String): AppResult<UserStatus> {
        return safeCall { api.checkVerificationStatus(sessionId) }.toAppResult { dto ->
            val status = dto.status.toDomainStatus()
            if (status == UserStatus.CONFIRMED) {
                // Update local session status if confirmed
                sessionStore.getSession().getOrNull()?.let { current ->
                    sessionStore.saveSession(current.copy(status = status.toSessionStatus()))
                }
            }
            status
        }
    }

    private suspend fun handleAuthResponse(call: suspend () -> retrofit2.Response<AuthResponseDto>): AppResult<User> {
        return safeCall(call = call).toAppResult { dto ->
            val user = dto.user.toDomain()
            sessionStore.saveSession(
                Session(
                    userId = user.id,
                    token = dto.accessToken,
                    refreshToken = dto.refreshToken,
                    status = user.status.toSessionStatus(),
                    userName = "${user.firstName} ${user.lastName}",
                    userPhone = user.phone
                )
            )
            user
        }
    }

    private fun getDeviceDto() = DeviceDto(
        installationId = "TODO_INSTALLATION_ID",
        deviceName = android.os.Build.MODEL,
        appVersion = "1.0.0"
    )

    private fun String.toDomainStatus(): UserStatus = when (this.lowercase()) {
        "confirmed" -> UserStatus.CONFIRMED
        "blocked" -> UserStatus.BLOCKED
        else -> UserStatus.UNCONFIRMED
    }

    private suspend fun <T, R> NetworkResult<T>.toAppResult(mapper: suspend (T) -> R): AppResult<R> {
        return when (this) {
            is NetworkResult.Success -> AppResult.Success(mapper(data))
            is NetworkResult.Error -> AppResult.Error(error.toAppError())
        }
    }
}
