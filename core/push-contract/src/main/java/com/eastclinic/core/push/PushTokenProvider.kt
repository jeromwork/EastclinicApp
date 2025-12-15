package com.eastclinic.core.push

import com.eastclinic.core.common.Result

/**
 * Interface for providing push token.
 */
interface PushTokenProvider {
    suspend fun getToken(): Result<String>
    suspend fun refreshToken(): Result<String>
}
