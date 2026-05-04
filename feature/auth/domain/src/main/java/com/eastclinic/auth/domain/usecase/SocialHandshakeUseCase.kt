package com.eastclinic.auth.domain.usecase

import com.eastclinic.auth.domain.model.User
import com.eastclinic.auth.domain.repository.AuthRepository
import com.eastclinic.core.common.Result
import javax.inject.Inject

/**
 * Use case for background social authentication (Handshake).
 */
class SocialHandshakeUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(provider: String, idToken: String): Result<User> {
        return repository.handshakeSocial(provider, idToken)
    }
}
