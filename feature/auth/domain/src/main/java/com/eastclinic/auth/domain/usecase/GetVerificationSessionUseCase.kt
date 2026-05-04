package com.eastclinic.auth.domain.usecase

import com.eastclinic.auth.domain.model.VerificationSession
import com.eastclinic.auth.domain.repository.AuthRepository
import com.eastclinic.core.common.Result
import javax.inject.Inject

/**
 * Use case for starting a verification session.
 */
class GetVerificationSessionUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<VerificationSession> {
        return repository.createVerificationSession()
    }
}
