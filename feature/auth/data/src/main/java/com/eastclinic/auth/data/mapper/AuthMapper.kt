package com.eastclinic.auth.data.mapper

import com.eastclinic.auth.data.api.dto.UserDto
import com.eastclinic.auth.data.api.dto.VerificationSessionDto
import com.eastclinic.auth.domain.model.User
import com.eastclinic.auth.domain.model.UserStatus
import com.eastclinic.auth.domain.model.VerificationSession
import com.eastclinic.core.auth.SessionUserStatus
import java.time.Instant

fun UserDto.toDomain(): User = User(
    id = id,
    firstName = firstName,
    lastName = lastName,
    middleName = middleName,
    phone = phone,
    email = email,
    status = status.toUserStatus()
)

fun String.toUserStatus(): UserStatus = when (this.lowercase()) {
    "confirmed" -> UserStatus.CONFIRMED
    "blocked" -> UserStatus.BLOCKED
    else -> UserStatus.UNCONFIRMED
}

fun UserStatus.toSessionStatus(): SessionUserStatus = when (this) {
    UserStatus.CONFIRMED -> SessionUserStatus.CONFIRMED
    UserStatus.BLOCKED -> SessionUserStatus.BLOCKED
    UserStatus.UNCONFIRMED -> SessionUserStatus.UNCONFIRMED
}

fun VerificationSessionDto.toDomain(): VerificationSession = VerificationSession(
    sessionId = sessionId,
    qrPayload = qrPayload,
    shortCode = shortCode,
    expiresAt = try {
        Instant.parse(expiresAt)
    } catch (e: Exception) {
        Instant.now().plusSeconds(300)
    }
)
