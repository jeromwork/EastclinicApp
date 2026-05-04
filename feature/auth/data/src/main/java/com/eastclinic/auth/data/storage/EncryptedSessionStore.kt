package com.eastclinic.auth.data.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.eastclinic.core.auth.Session
import com.eastclinic.core.auth.SessionStore
import com.eastclinic.core.auth.SessionUserStatus
import com.eastclinic.core.common.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedSessionStore @Inject constructor(
    @ApplicationContext private val context: Context
) : SessionStore {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_session_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override suspend fun saveSession(session: Session): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            prefs.edit().apply {
                putString(KEY_USER_ID, session.userId)
                putString(KEY_TOKEN, session.token)
                putString(KEY_REFRESH_TOKEN, session.refreshToken)
                putString(KEY_STATUS, session.status.name)
                putString(KEY_USER_NAME, session.userName)
                putString(KEY_USER_PHONE, session.userPhone)
            }.apply()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(com.eastclinic.core.common.AppError.UnknownError("Failed to save session", e))
        }
    }

    override suspend fun getSession(): Result<Session?> = withContext(Dispatchers.IO) {
        try {
            val userId = prefs.getString(KEY_USER_ID, null) ?: return@withContext Result.Success(null)
            val token = prefs.getString(KEY_TOKEN, null) ?: return@withContext Result.Success(null)
            
            val session = Session(
                userId = userId,
                token = token,
                refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null),
                status = try {
                    SessionUserStatus.valueOf(prefs.getString(KEY_STATUS, SessionUserStatus.UNCONFIRMED.name)!!)
                } catch (e: Exception) {
                    SessionUserStatus.UNCONFIRMED
                },
                userName = prefs.getString(KEY_USER_NAME, null),
                userPhone = prefs.getString(KEY_USER_PHONE, null)
            )
            Result.Success(session)
        } catch (e: Exception) {
            Result.Error(com.eastclinic.core.common.AppError.UnknownError("Failed to read session", e))
        }
    }

    override suspend fun clearSession(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            prefs.edit().clear().apply()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(com.eastclinic.core.common.AppError.UnknownError("Failed to clear session", e))
        }
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_TOKEN = "token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_STATUS = "status"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_PHONE = "user_phone"
    }
}
