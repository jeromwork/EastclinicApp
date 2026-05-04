package com.eastclinic.auth.data.social

import com.eastclinic.core.common.Result

/**
 * Interface for platform-specific social authentication SDKs (Google, Apple).
 */
interface SocialAuthService {
    /**
     * Triggers the social login flow and returns the ID token.
     */
    suspend fun signIn(): Result<String>
    
    /**
     * Signs out from the social provider.
     */
    suspend fun signOut(): Result<Unit>
}
