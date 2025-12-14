# Data Model: Step 0 — Contract skeleton

**Feature**: Step 0 — Contract skeleton (Architecture baseline)  
**Date**: 2025-01-27

## Overview

На Step 0 реальные доменные модели не создаются — только базовые типы и контракты для архитектурного каркаса. Доменные модели будут созданы при реализации конкретных фич.

## Core Types

### AppError (core:common)

Sealed class для нормализованного представления ошибок во всех слоях приложения.

```kotlin
sealed class AppError {
    data class NetworkError(
        val message: String,
        val code: Int? = null
    ) : AppError()
    
    data class ValidationError(
        val field: String? = null,
        val message: String
    ) : AppError()
    
    data class UnknownError(
        val message: String,
        val cause: Throwable? = null
    ) : AppError()
    
    // Другие базовые типы ошибок по мере необходимости
}
```

**Размещение**: `core:common/src/main/java/com/eastclinic/core/common/AppError.kt`

### Result<T> (core:common)

Sealed class для type-safe представления результата операции.

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: AppError) : Result<Nothing>()
}
```

**Размещение**: `core:common/src/main/java/com/eastclinic/core/common/Result.kt`

### DispatcherProvider (core:async)

Интерфейс для предоставления coroutine dispatchers (для тестируемости).

```kotlin
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}
```

**Размещение**: `core:async/src/main/java/com/eastclinic/core/async/DispatcherProvider.kt`

### Clock (core:async)

Интерфейс для работы со временем (для тестируемости).

```kotlin
interface Clock {
    fun now(): Instant
    fun nowMillis(): Long
}
```

**Размещение**: `core:async/src/main/java/com/eastclinic/core/async/Clock.kt`

### UiEffect (core:ui)

Sealed class для одноразовых UI событий (навигация, сообщения).

```kotlin
sealed class UiEffect {
    data class Navigate(val route: String) : UiEffect()
    data class ShowMessage(val message: String) : UiEffect()
    // Feature-модули могут расширять через композицию
}
```

**Размещение**: `core:ui/src/main/java/com/eastclinic/core/ui/UiEffect.kt`

### NetworkResult<T> (core:network)

Тип для результата сетевого запроса.

```kotlin
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val error: NetworkError) : NetworkResult<Nothing>()
}
```

**Размещение**: `core:network/src/main/java/com/eastclinic/core/network/NetworkResult.kt`

### NetworkError (core:network)

Ошибка сети (маппится в AppError.NetworkError).

```kotlin
data class NetworkError(
    val message: String,
    val code: Int? = null,
    val cause: Throwable? = null
)
```

**Размещение**: `core:network/src/main/java/com/eastclinic/core/network/NetworkError.kt`

## Contract Interfaces

### AuthTokenProvider (core:auth-contract)

Интерфейс для предоставления токена авторизации.

```kotlin
interface AuthTokenProvider {
    suspend fun getToken(): String?
    suspend fun refreshToken(): Result<String>
}
```

**Размещение**: `core:auth-contract/src/main/java/com/eastclinic/core/auth/AuthTokenProvider.kt`

### SessionStore (core:auth-contract)

Интерфейс для хранения сессии.

```kotlin
interface SessionStore {
    suspend fun saveSession(session: Session): Result<Unit>
    suspend fun getSession(): Result<Session?>
    suspend fun clearSession(): Result<Unit>
}
```

**Размещение**: `core:auth-contract/src/main/java/com/eastclinic/core/auth/SessionStore.kt`

### PushTokenProvider (core:push-contract)

Интерфейс для предоставления push токена.

```kotlin
interface PushTokenProvider {
    suspend fun getToken(): Result<String>
    suspend fun refreshToken(): Result<String>
}
```

**Размещение**: `core:push-contract/src/main/java/com/eastclinic/core/push/PushTokenProvider.kt`

### NotificationHandler (core:push-contract)

Интерфейс для обработки уведомлений.

```kotlin
interface NotificationHandler {
    fun handleNotification(notification: Notification)
    fun handleNotificationClick(notificationId: String)
}
```

**Размещение**: `core:push-contract/src/main/java/com/eastclinic/core/push/NotificationHandler.kt`

## Feature Domain Models (Stub)

На Step 0 создаются только stub-модели для демонстрации структуры. Реальные модели будут созданы при реализации фич.

### Auth Domain (feature:auth:domain)

```kotlin
// Stub модель для демонстрации
data class User(
    val id: String,
    val name: String
)
```

### Other Features

Аналогично создаются stub-модели для:
- Clinics
- Doctors
- Appointments
- Chat

## Validation Rules

На Step 0 валидация не реализуется — только структура. Валидация будет добавлена при реализации конкретных фич.

## State Transitions

На Step 0 state transitions не определяются — только базовые типы для UI состояния (UiState, UiEvent, UiEffect).

## Notes

- Все типы в domain слоях не содержат Android/Retrofit/Room типов
- Feature-модули могут определять свои доменные ошибки, которые маппятся в AppError через data слой
- NetworkError маппится в AppError.NetworkError в core:network

