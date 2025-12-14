# Network Contracts: Step 0

**Feature**: Step 0 — Contract skeleton (Architecture baseline)  
**Date**: 2025-01-27

## Overview

На Step 0 реальные API endpoints не определяются (backend уже существует). Создаются только интерфейсы и контракты в `core:network` для безопасной работы с сетью без утечки Retrofit типов в domain/presentation слои.

## Core Network Types

### NetworkResult<T>

Результат сетевого запроса.

```kotlin
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val error: NetworkError) : NetworkResult<Nothing>()
}
```

### NetworkError

Ошибка сети (маппится в AppError.NetworkError).

```kotlin
data class NetworkError(
    val message: String,
    val code: Int? = null,
    val cause: Throwable? = null
)
```

## Safe Call Function

Функция-обёртка для безопасных сетевых вызовов с автоматическим маппингом ошибок.

```kotlin
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
```

## Error Mapping

NetworkError маппится в AppError.NetworkError в data слоях feature модулей:

```kotlin
fun NetworkError.toAppError(): AppError {
    return AppError.NetworkError(
        message = this.message,
        code = this.code
    )
}
```

## Interceptors

### Logging Interceptor

Для debug логирования запросов/ответов (только в debug сборке).

### Auth Interceptor

Добавляет токен авторизации в заголовки через AuthTokenProvider (контракт из core:auth-contract).

### Headers Interceptor

Добавляет общие заголовки (User-Agent, Content-Type и т.д.).

## Retrofit Configuration

Retrofit настраивается в core:network, но его типы не утекают наружу:

- Retrofit instance создаётся в core:network
- API интерфейсы (Retrofit) остаются в core:network или feature:data
- Domain слой не знает о Retrofit
- Presentation слой не знает о Retrofit

## Future API Integration

При интеграции с реальным backend:

1. API интерфейсы создаются в `feature:<x>:data/api/`
2. DTO классы создаются в `feature:<x>:data/dto/`
3. Маппинг DTO → Domain model в `feature:<x>:data/mapper/`
4. Repository реализует domain интерфейс, используя API через safeCall

## Notes

- Все сетевые ошибки нормализуются в AppError
- NetworkError не утекает в domain/presentation
- safeCall обеспечивает единообразную обработку ошибок
- Интерцепторы настраиваются через Hilt в core:network DI модуле

