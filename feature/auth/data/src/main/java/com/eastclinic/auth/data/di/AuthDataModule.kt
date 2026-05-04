package com.eastclinic.auth.data.di

import com.eastclinic.auth.data.api.AuthApi
import com.eastclinic.auth.data.repository.AuthRepositoryImpl
import com.eastclinic.auth.data.storage.EncryptedSessionStore
import com.eastclinic.auth.domain.repository.AuthRepository
import com.eastclinic.core.auth.SessionStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthDataModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSessionStore(
        encryptedSessionStore: EncryptedSessionStore
    ): SessionStore

    @Binds
    @Singleton
    abstract fun bindAuthTokenProvider(
        authTokenProviderImpl: com.eastclinic.auth.data.storage.AuthTokenProviderImpl
    ): com.eastclinic.core.auth.AuthTokenProvider

    @Binds
    @Singleton
    abstract fun bindSocialAuthService(
        googleAuthService: com.eastclinic.auth.data.social.GoogleAuthService
    ): com.eastclinic.auth.data.social.SocialAuthService

    companion object {
        @Provides
        @Singleton
        fun provideAuthApi(retrofit: Retrofit): AuthApi {
            return retrofit.create(AuthApi::class.java)
        }

        @Provides
        @Singleton
        fun provideRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://api.eastclinic.ru") // Placeholder
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}



