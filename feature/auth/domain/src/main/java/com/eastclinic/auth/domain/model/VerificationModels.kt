package com.eastclinic.auth.domain.model

import java.time.Instant

/**
 * Session for QR-code based profile verification.
 */
data class VerificationSession(
    val sessionId: String,
    val qrPayload: String,
    val shortCode: String,
    val expiresAt: Instant
)

/**
 * User profile data for registration/bootstrap.
 */
data class UserProfile(
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val phone: String
)
