package com.eastclinic.auth.data.api.dto

import com.google.gson.annotations.SerializedName

data class SocialAuthRequestDto(
    @SerializedName("provider") val provider: String,
    @SerializedName("id_token") val idToken: String,
    @SerializedName("device") val device: DeviceDto
)

data class SocialBootstrapRequestDto(
    @SerializedName("provider") val provider: String,
    @SerializedName("id_token") val idToken: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("middle_name") val middleName: String?,
    @SerializedName("phone") val phone: String,
    @SerializedName("device") val device: DeviceDto
)

data class DeviceDto(
    @SerializedName("platform") val platform: String = "android",
    @SerializedName("installation_id") val installationId: String,
    @SerializedName("device_name") val deviceName: String,
    @SerializedName("app_version") val appVersion: String
)

data class AuthResponseDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("user") val user: UserDto
)

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("middle_name") val middleName: String?,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String,
    @SerializedName("status") val status: String
)

data class VerificationSessionDto(
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("qr_payload") val qrPayload: String,
    @SerializedName("short_code") val shortCode: String,
    @SerializedName("expires_at") val expiresAt: String
)

data class VerificationStatusDto(
    @SerializedName("status") val status: String
)
