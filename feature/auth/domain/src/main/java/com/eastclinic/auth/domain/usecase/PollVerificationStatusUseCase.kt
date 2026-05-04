package com.eastclinic.auth.domain.usecase

import com.eastclinic.auth.domain.model.UserStatus
import com.eastclinic.auth.domain.repository.AuthRepository
import com.eastclinic.core.common.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for polling the verification status.
 */
class PollVerificationStatusUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(sessionId: String, intervalMs: Long = 3000L): Flow<Result<UserStatus>> = flow {
        while (true) {
            val result = repository.checkVerificationStatus(sessionId)
            emit(result)
            
            if (result is Result.Success && result.data == UserStatus.CONFIRMED) {
                break
            }
            
            delay(intervalMs)
        }
    }
}
