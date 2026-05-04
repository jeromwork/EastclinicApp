package com.eastclinic.auth.data.api

import com.eastclinic.auth.data.api.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {
    @POST("/api/auth/social/handshake")
    suspend fun handshake(@Body request: SocialAuthRequestDto): Response<AuthResponseDto>

    @POST("/api/auth/social/bootstrap")
    suspend fun bootstrap(@Body request: SocialBootstrapRequestDto): Response<AuthResponseDto>

    @POST("/api/auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("/api/profile/verification-session")
    suspend fun createVerificationSession(): Response<VerificationSessionDto>

    @GET("/api/profile/verification-session/{id}")
    suspend fun checkVerificationStatus(@Path("id") sessionId: String): Response<VerificationStatusDto>
}
