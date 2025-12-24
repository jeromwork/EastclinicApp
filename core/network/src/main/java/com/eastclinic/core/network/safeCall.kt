package com.eastclinic.core.network

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Safe wrapper function for network calls with automatic error mapping.
 */
suspend fun <T> safeCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    call: suspend () -> Response<T>
): NetworkResult<T> {
    return try {
        val response = withContext(dispatcher) { call() }
        if (response.isSuccessful) {
            NetworkResult.Success(response.body() ?: throw IllegalStateException("Body is null"))
        } else {
            NetworkResult.Error(
                NetworkError(
                    message = response.message(),
                    code = response.code()
                )
            )
        }
    } catch (e: Exception) {
        NetworkResult.Error(
            NetworkError(
                message = e.message ?: "Unknown error",
                cause = e
            )
        )
    }
}


