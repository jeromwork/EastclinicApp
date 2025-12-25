# Блок 3: Core модули

## Обзор

Обновлены все core модули для Step 1: SDK 24/35, JUnit5, правильные зависимости.

## Модули

### 1. core:common

**Назначение**: Базовые типы без Android-зависимостей.

**Файлы**:
- `Result.kt` — sealed class для результатов операций
- `AppError.kt` — sealed class для ошибок

**Пример кода**:

```kotlin
// Result.kt
package com.eastclinic.core.common

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: AppError) : Result<Nothing>()
}
```

```kotlin
// AppError.kt
package com.eastclinic.core.common

sealed class AppError {
    abstract val message: String
    
    data class NetworkError(
        override val message: String,
        val code: Int? = null
    ) : AppError()
    
    data class ValidationError(
        val field: String? = null,
        override val message: String
    ) : AppError()
    
    data class UnknownError(
        override val message: String,
        val cause: Throwable? = null
    ) : AppError()
}
```

**build.gradle.kts**:
```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    testImplementation(libs.bundles.test.unit)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)
}
```

### 2. core:async

**Назначение**: Абстракции для корутин и времени (для тестируемости).

**Файлы**:
- `DispatcherProvider.kt` — интерфейс для диспетчеров
- `Clock.kt` — интерфейс для времени
- `TestDispatchers.kt` — тестовая реализация
- `FakeClock.kt` — тестовая реализация

**Пример кода**:

```kotlin
// DispatcherProvider.kt
package com.eastclinic.core.async

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}
```

```kotlin
// Clock.kt
package com.eastclinic.core.async

import java.time.Instant

interface Clock {
    fun now(): Instant
    fun nowMillis(): Long
}
```

```kotlin
// TestDispatchers.kt
package com.eastclinic.core.async

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class TestDispatchers(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : DispatcherProvider {
    override val main: CoroutineDispatcher = testDispatcher
    override val io: CoroutineDispatcher = testDispatcher
    override val default: CoroutineDispatcher = testDispatcher
    override val unconfined: CoroutineDispatcher = testDispatcher
}

class DefaultDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}
```

```kotlin
// FakeClock.kt
package com.eastclinic.core.async

import java.time.Instant

class FakeClock(
    private var currentTime: Instant = Instant.now()
) : Clock {
    override fun now(): Instant = currentTime
    
    override fun nowMillis(): Long = currentTime.toEpochMilli()
    
    fun advanceTime(seconds: Long) {
        currentTime = currentTime.plusSeconds(seconds)
    }
    
    fun setTime(instant: Instant) {
        currentTime = instant
    }
}
```

**build.gradle.kts**:
```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.bundles.coroutines)
    
    testImplementation(libs.bundles.test.unit)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)
}
```

### 3. core:ui

**Назначение**: UI компоненты, тема, навигация.

**Файлы**:
- `UiEffect.kt` — маркерный интерфейс для UI эффектов
- `AppDestination.kt` — интерфейс для навигационных маршрутов
- `theme/Theme.kt` — тема приложения

**Пример кода**:

```kotlin
// UiEffect.kt
package com.eastclinic.core.ui

interface UiEffect
```

```kotlin
// AppDestination.kt
package com.eastclinic.core.ui

interface AppDestination {
    val route: String
}
```

**build.gradle.kts**:
```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.eastclinic.core.ui"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }
    
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
}
```

### 4. core:network

**Назначение**: Сетевой слой (обновлен только для JUnit5).

**build.gradle.kts**:
```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:auth-contract"))
    implementation(libs.bundles.network)
    implementation(libs.bundles.coroutines)
    
    testImplementation(libs.bundles.test.unit)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)
}
```

## Зависимости между core модулями

```
core:common (базовые типы)
    ↑
    ├── core:async (зависит от core:common)
    ├── core:ui (зависит от core:common)
    └── core:network (зависит от core:common, core:auth-contract)
```

**Правила**:
- ✅ Все core модули могут зависеть от `core:common`
- ✅ `core:network` может зависеть от `core:auth-contract`
- ❌ Нет циклических зависимостей
- ❌ `core:auth-contract` НЕ зависит от `core:network`

## Коммит

**ID**: `f54fd62`  
**Сообщение**: `build: update core modules for Step1 (SDK 24/35, JUnit5)`  
**Файлов**: 4  
**Изменений**: +17 / -12

## Проверка

✅ Все core модули используют SDK 24/35  
✅ JUnit5 настроен во всех модулях  
✅ Зависимости корректны (нет циклов)  
✅ Domain-модули не содержат Android типов


