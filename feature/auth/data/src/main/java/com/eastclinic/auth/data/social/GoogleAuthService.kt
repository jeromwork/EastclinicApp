package com.eastclinic.auth.data.social

import com.eastclinic.core.common.Result
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthService @Inject constructor() : SocialAuthService {
    override suspend fun signIn(): Result<String> {
        delay(1000) // Simulate SDK work
        return Result.Success("dummy_google_id_token")
    }

    override suspend fun signOut(): Result<Unit> {
        return Result.Success(Unit)
    }
}
