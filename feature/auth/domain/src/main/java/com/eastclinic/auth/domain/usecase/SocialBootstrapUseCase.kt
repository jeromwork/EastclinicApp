package com.eastclinic.auth.domain.usecase

import com.eastclinic.auth.domain.model.User
import com.eastclinic.auth.domain.model.UserProfile
import com.eastclinic.auth.domain.repository.AuthRepository
import com.eastclinic.core.common.Result
import javax.inject.Inject

/**
 * Use case for initial social registration (Bootstrap).
 */
class SocialBootstrapUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        provider: String, 
        idToken: String, 
        profile: UserProfile
    ): Result<User> {
        return repository.bootstrapSocial(provider, idToken, profile)
    }
}
