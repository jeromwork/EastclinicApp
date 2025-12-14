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

NetworkError маппится в AppError.NetworkError в data слоях feature модулей. Маппинг реализуется через extension функцию в core:network:

```kotlin
// В core:network/src/main/java/com/eastclinic/core/network/NetworkErrorMapper.kt
fun NetworkError.toAppError(): AppError {
    return AppError.NetworkError(
        message = this.message,
        code = this.code
    )
}

// Использование в feature:data слоях:
suspend fun getData(): Result<Data> {
    return when (val networkResult = safeCall { api.getData() }) {
        is NetworkResult.Success -> Result.Success(networkResult.data.toDomain())
        is NetworkResult.Error -> Result.Error(networkResult.error.toAppError())
    }
}
```

**Важно**: Маппинг происходит в data слое, domain и presentation видят только AppError.

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

## Flow Error Handling

При использовании Flow для асинхронных операций, ошибки обрабатываются через `catch` оператор с маппингом в AppError:

```kotlin
// В feature:data слое
fun observeData(): Flow<Result<Data>> = flow {
    emit(Result.Success(fetchData()))
}.catch { exception ->
    val networkError = when (exception) {
        is HttpException -> NetworkError(
            message = exception.message(),
            code = exception.code()
        )
        is IOException -> NetworkError(
            message = "Network error: ${exception.message}",
            cause = exception
        )
        else -> NetworkError(
            message = "Unknown error: ${exception.message}",
            cause = exception
        )
    }
    emit(Result.Error(networkError.toAppError()))
}.flowOn(Dispatchers.IO)
```

**Паттерн**: Все Flow операции в data слое должны использовать `catch` для обработки ошибок и маппинга в AppError перед передачей в domain слой.

## Notes

- Все сетевые ошибки нормализуются в AppError
- NetworkError не утекает в domain/presentation
- safeCall обеспечивает единообразную обработку ошибок
- Flow операции используют `catch` для обработки ошибок
- Интерцепторы настраиваются через Hilt в core:network DI модуле
- Маппинг NetworkError → AppError происходит в data слое через extension функцию

