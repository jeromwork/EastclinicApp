package com.eastclinic.auth.domain.repository

import com.eastclinic.auth.domain.model.User
import com.eastclinic.auth.domain.model.UserStatus
import com.eastclinic.core.common.Result

import com.eastclinic.auth.domain.model.UserProfile
import com.eastclinic.auth.domain.model.VerificationSession

/**
 * Auth repository interface.
 */
interface AuthRepository {
    suspend fun login(username: String, password: String): Result<User>
    
    /**
     * Authenticates using a social provider ID token (Handshake).
     */
    suspend fun handshakeSocial(provider: String, idToken: String): Result<User>
    
    /**
     * Creates a new user with profile info using a social provider ID token (Bootstrap).
     */
    suspend fun bootstrapSocial(provider: String, idToken: String, profile: UserProfile): Result<User>
    
    suspend fun logout(): Result<Unit>
    
    /**
     * Starts a profile verification session.
     */
    suspend fun createVerificationSession(): Result<VerificationSession>
    
    /**
     * Checks the status of a verification session (Polling).
     */
    suspend fun checkVerificationStatus(sessionId: String): Result<UserStatus>
}



