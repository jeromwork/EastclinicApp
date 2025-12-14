# Стратегия тестирования: Step 0 — Contract skeleton

**Feature**: Step 0 — Contract skeleton (Architecture baseline)  
**Date**: 2025-01-27

## Обзор

Документ описывает полную стратегию тестирования для Step 0, включая используемые библиотеки, типы тестов, подходы и примеры.

---

## 1. Технологический стек для тестирования

### Библиотеки (Must-Have для Step 0)

- **JUnit 4** — для всех тестов (стандарт Android экосистемы, проще и стабильнее для Step 0)
- **MockK** — для создания моков (альтернатива Mockito, лучше работает с Kotlin)
- **Turbine** — для тестирования Flow (реактивные потоки)
- **kotlinx-coroutines-test** — для тестирования корутин (StandardTestDispatcher, UnconfinedTestDispatcher)
- **Compose Testing** (compose-ui-test-junit4) — для UI тестов Compose компонентов (опционально для Step 0)

### Библиотеки (Optional для Step 0)

- **Robolectric** — для Android-тестов без эмулятора (не нужен на Step 0, добавить позже при необходимости)
- **Espresso** — для интеграционных UI тестов (не используется на Step 0)

### Управление версиями

Все версии тестовых библиотек управляются через `gradle/libs.versions.toml` (version catalog).

### Выбор JUnit версии

**Решение для Step 0**: JUnit 4 везде (проще, стабильнее, стандарт Android экосистемы)

**Альтернатива** (если нужен JUnit 5):
- JUnit 5 только для чистых JVM модулей (core:common, core:async если они kotlin("jvm"))
- JUnit 4 для Android модулей и Compose UI тестов

**Рекомендация**: На Step 0 использовать JUnit 4 везде для простоты и стабильности.

---

## 2. Типы тестов и их назначение

### 2.1 Unit Tests (Модульные тесты)

**Назначение**: Тестирование отдельных компонентов изолированно.

**Что тестируется на Step 0**:

1. **Базовые типы (core:common)**:
   - `Result<T>` — все варианты sealed class (Success, Error)
   - `AppError` — все варианты sealed class (NetworkError, ValidationError, UnknownError и т.д.)
   - **Требование**: Покрытие всех sealed class variants (для Result и AppError это быстро и необходимо)

2. **Маппинг ошибок (core:network)**:
   - `NetworkError.toAppError()` — правильность маппинга NetworkError в AppError.NetworkError
   - Тест в `NetworkErrorMapperTest.kt`

3. **Тестовые утилиты (core:async)**:
   - `FakeClock` — корректность работы с временем в тестах
   - `TestDispatchers` — корректность предоставления dispatchers для тестов

4. **ViewModel (пример)**:
   - Один пример теста ViewModel для демонстрации паттерна (SC-016)
   - Тест в `LoginViewModelTest.kt` (feature:auth:presentation)

**Где находятся**: `src/test/java/...` в соответствующих модулях

**Команды запуска**:
```bash
# Все unit тесты
./gradlew test

# Тесты конкретного модуля
./gradlew :core:common:test
./gradlew :feature:auth:presentation:test
```

---

### 2.2 Build-Time Checks (Проверки на этапе сборки)

**Назначение**: Проверка правильности архитектурных границ и зависимостей между модулями на этапе сборки.

**Подход**: Вместо runtime тестов, которые парсят зависимости, используем build-time проверки через Gradle.

**Что проверяется на Step 0**:

1. **Domain модули — чистые JVM модули**:
   - Domain модули используют `kotlin("jvm")` плагин, а не `com.android.library`
   - Android классы физически недоступны (компилятор не даст их использовать)
   - Retrofit/Room зависимости не добавляются в domain модули (следует из правил зависимостей)

2. **Gradle dependency rules**:
   - Feature-to-feature зависимости запрещены на уровне Gradle (можно настроить через dependency constraints)
   - Core модули имеют ацикличный граф (Gradle не даст собрать при циклических зависимостях)
   - Запрет `core:auth-contract → core:network` обеспечивается отсутствием зависимости в `build.gradle.kts`

**Как проверяется**:
```bash
# Проверка зависимостей через Gradle
./gradlew :app:dependencies > dependencies.txt
# Ручная проверка отсутствия недопустимых зависимостей

# Проверка компиляции (если domain попытается использовать Android — не скомпилируется)
./gradlew :feature:auth:domain:build
```

**Где настраивается**: В `build.gradle.kts` каждого модуля через правильные плагины и зависимости.

**Важно**: Архитектурные ограничения проверяются сборкой и Gradle-правилами, а не runtime тестами.

---

### 2.3 UI Tests (UI тесты) — Optional для Step 0

**Статус**: Опционально для Step 0 (nice-to-have, не обязательны)

**Назначение**: Проверка UI компонентов и навигации на реальном устройстве/эмуляторе.

**Почему опционально**:
- Требуют эмулятор/устройство
- Увеличивают время CI
- Часто ломаются из-за текстов, локализации, верстки
- На Step 0 достаточно unit тестов и build checks

**Если реализуются, что тестируется**:

1. **Smoke test навигации**:
   - Старт приложения
   - Переход 1-2 шага (например, Login → Home)
   - Проверка back navigation
   - Тест в `NavigationSmokeTest.kt` (app модуль)

**Где находятся**: `app/src/androidTest/java/com/eastclinic/app/...`

**Команды запуска**:
```bash
# Требуется запущенный эмулятор или подключенное устройство
./gradlew :app:connectedAndroidTest
```

**Библиотека**: Compose Testing (compose-ui-test-junit4)

**Важно**: UI тесты должны быть устойчивыми:
- Использовать `testTag` вместо текста для поиска элементов
- Использовать semantics для поиска элементов
- Делать минимальный smoke test, а не полное покрытие

---

## 3. Тестовые утилиты (Test Fixtures)

**Назначение**: Переиспользуемые компоненты для упрощения написания тестов и обеспечения консистентности.

### 3.1 FakeClock (core:async)

**Расположение**: `core/async/src/test/java/com/eastclinic/core/async/FakeClock.kt`

**Назначение**: Контролируемый источник времени для тестов.

**Использование**:
```kotlin
val fakeClock = FakeClock()
fakeClock.setTime(Instant.parse("2025-01-27T10:00:00Z"))
// Тест использует fakeClock.now()
```

**Зачем**: Позволяет тестировать временные зависимости без реального ожидания.

### 3.2 TestDispatchers (core:async)

**Расположение**: `core/async/src/test/java/com/eastclinic/core/async/TestDispatchers.kt`

**Назначение**: Тестовые dispatchers для корутин на основе `kotlinx-coroutines-test`.

**Использование**:
```kotlin
// Использование StandardTestDispatcher (контролируемое выполнение)
val testDispatcher = StandardTestDispatcher()
val viewModel = MyViewModel(repository, DispatcherProvider(testDispatcher))

// Или UnconfinedTestDispatcher (немедленное выполнение)
val unconfinedDispatcher = UnconfinedTestDispatcher()
```

**Библиотека**: `kotlinx-coroutines-test`

**Зачем**: Позволяет тестировать асинхронный код синхронно и предсказуемо.

**Важно**: 
- Использовать `kotlinx-coroutines-test` как стандарт
- Контролировать Main dispatcher через `Dispatchers.setMain(testDispatcher)` если используется
- Выбирать между StandardTestDispatcher (контролируемое) и UnconfinedTestDispatcher (немедленное) осознанно

### 3.3 ResultFactory (core:common)

**Расположение**: `core/common/src/test/java/com/eastclinic/core/common/ResultFactory.kt`

**Назначение**: Фабрики для создания тестовых экземпляров Result<T>.

**Использование**:
```kotlin
val successResult = ResultFactory.success(data)
val errorResult = ResultFactory.error(AppError.NetworkError("Error"))
```

**Зачем**: Упрощает создание тестовых данных, обеспечивает консистентность.

### 3.4 AppErrorFactory (core:common)

**Расположение**: `core/common/src/test/java/com/eastclinic/core/common/AppErrorFactory.kt`

**Назначение**: Фабрики для создания тестовых экземпляров AppError.

**Использование**:
```kotlin
val networkError = AppErrorFactory.networkError("Network failed")
val validationError = AppErrorFactory.validationError("Invalid input")
```

**Зачем**: Упрощает создание тестовых ошибок, обеспечивает консистентность.

---

## 4. Паттерны тестирования

### 4.1 Тестирование ViewModel (UiState/UiEvent/UiEffect)

**Пример**:

```kotlin
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.StandardTestDispatcher

@Test
fun `when LoadUser event, then state updates correctly`() = runTest {
    // Given
    val testDispatcher = StandardTestDispatcher()
    val repository = mockk<UserRepository>()
    coEvery { repository.getUser("1") } returns Result.Success(User("1", "Test"))
    
    // When
    val viewModel = UserViewModel(repository, DispatcherProvider(testDispatcher))
    viewModel.handleEvent(UserUiEvent.LoadUser("1"))
    testDispatcher.scheduler.advanceUntilIdle() // Запускаем все корутины
    
    // Then
    val state = viewModel.uiState.value
    assertEquals(false, state.isLoading)
    assertEquals("Test", state.user?.name)
}
```

**Библиотеки**:
- `kotlinx-coroutines-test` для `runTest` и `StandardTestDispatcher`
- MockK для моков
- JUnit 4 для тестового фреймворка

**Что тестируется**:
- Обработка UiEvent
- Обновление UiState через StateFlow
- Эмиссия UiEffect через SharedFlow (если нужно)

**Паттерн**:
1. **Given** — настройка моков и начального состояния
2. **When** — выполнение действия (handleEvent)
3. **Then** — проверка результата (проверка UiState)

**Используемые библиотеки**:
- MockK для моков репозиториев
- TestDispatchers (на основе kotlinx-coroutines-test) для контролируемого выполнения корутин
- `runTest` из kotlinx-coroutines-test для тестирования корутин
- JUnit 4 для тестового фреймворка

---

### 4.2 Тестирование Flow операций

**Для тестирования Flow используется Turbine**:

```kotlin
import kotlinx.coroutines.test.runTest
import app.cash.turbine.test

@Test
fun `observeUsers emits success result`() = runTest {
    // Given
    val repository = mockk<UserRepository>()
    val users = listOf(User("1", "Test"))
    every { repository.observeUsers() } returns flowOf(Result.Success(users))
    
    // When & Then
    repository.observeUsers().test {
        val result = awaitItem()
        assertEquals(Result.Success(users), result)
        awaitComplete()
    }
}
```

**Библиотеки**:
- Turbine для тестирования Flow
- `kotlinx-coroutines-test` для `runTest`

**Что тестируется**:
- Эмиссия значений через Flow
- Обработка ошибок через catch
- Завершение Flow

**Библиотека**: Turbine (`test { }` блок)

---

### 4.3 Тестирование UI (Compose Testing)

**Пример (устойчивый, с testTag)**:

```kotlin
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule

@get:Rule
val composeTestRule = createComposeRule()

@Test
fun testNavigationSmoke() {
    composeTestRule.setContent {
        RootNavGraph()
    }
    
    // Используем testTag вместо текста (устойчиво к локализации и изменениям UI)
    composeTestRule.onNodeWithTag("login_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("login_button").performClick()
    composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()
    
    // Проверка back navigation
    composeTestRule.onNodeWithTag("back_button").performClick()
    composeTestRule.onNodeWithTag("login_screen").assertIsDisplayed()
}
```

**Важно для устойчивости**:
- Использовать `testTag` вместо `onNodeWithText()` — не зависит от текста и локализации
- Использовать semantics для поиска элементов
- Делать минимальный smoke test (1-2 перехода), а не полное покрытие

**Что тестируется**:
- Отображение UI элементов
- Навигация между экранами
- Взаимодействие пользователя с UI

**Паттерн**:
1. `setContent { }` — установка Compose контента
2. `onNodeWith*()` — поиск элементов UI
3. `performClick()` / `performTextInput()` — действия пользователя
4. `assertIsDisplayed()` / `assertExists()` — проверки

**Библиотека**: Compose Testing

---

### 4.4 Тестирование маппинга ошибок

**Пример для NetworkError → AppError**:

```kotlin
@Test
fun `NetworkError maps to AppError.NetworkError correctly`() {
    // Given
    val networkError = NetworkError(
        message = "Network failed",
        code = 500
    )
    
    // When
    val appError = networkError.toAppError()
    
    // Then
    assertTrue(appError is AppError.NetworkError)
    assertEquals("Network failed", appError.message)
    assertEquals(500, (appError as AppError.NetworkError).code)
}
```

**Что тестируется**:
- Правильность маппинга NetworkError в AppError
- Сохранение всех полей при маппинге

---

## 5. Что должно тестироваться на Step 0

### 5.0 Must-Have vs Optional

**Must-Have (обязательно для Step 0)**:
- Unit тесты базовых типов (Result, AppError)
- Unit тест маппинга ошибок
- Пример ViewModel теста
- Тестовые утилиты (FakeClock, TestDispatchers, фабрики)
- Build-time проверки архитектурных правил
- CI: `./gradlew build` и `./gradlew test`

**Optional (можно, но не обязательно для Step 0)**:
- UI тесты навигации (1 smoke test)
- Robolectric
- Интеграционные тесты зависимостей как runtime тесты (лучше build checks)

---

## 5.1 Must-Have тесты (обязательно)

### 5.2 Optional тесты (можно отложить)

#### ✅ Unit тесты для базовых типов (SC-014)

**Result<T>**:
- Тест для `Result.Success<T>` — проверка данных
- Тест для `Result.Error` — проверка ошибки
- Тест для всех методов расширения (если есть)

**AppError**:
- Тест для каждого варианта sealed class:
  - `AppError.NetworkError`
  - `AppError.ValidationError`
  - `AppError.UnknownError`
  - И другие базовые типы
- **Требование**: Покрытие всех sealed class variants (для Result и AppError это быстро и необходимо)

**Расположение**:
- `core/common/src/test/java/com/eastclinic/core/common/ResultTest.kt`
- `core/common/src/test/java/com/eastclinic/core/common/AppErrorTest.kt`

#### ✅ Тест маппинга ошибок

**NetworkErrorMapper**:
- Тест для `NetworkError.toAppError()`
- Проверка всех полей при маппинге

**Расположение**:
- `core/network/src/test/java/com/eastclinic/core/network/NetworkErrorMapperTest.kt`

#### ✅ Пример теста ViewModel (SC-016)

**LoginViewModelTest**:
- Тест обработки UiEvent
- Тест обновления UiState
- Тест эмиссии UiEffect (если нужно)

**Расположение**:
- `feature/auth/presentation/src/test/java/com/eastclinic/auth/presentation/login/LoginViewModelTest.kt`

#### ✅ Build-Time Checks (SC-015)

**Проверка через Gradle и компиляцию**:
- Domain модули используют `kotlin("jvm")` плагин → Android классы недоступны
- Retrofit/Room зависимости не добавляются в domain → компилятор не даст использовать
- Feature-to-feature зависимости запрещены через Gradle dependency rules
- Ацикличность core модулей проверяется сборкой (Gradle не даст собрать при циклах)

**Как проверяется**:
```bash
# Проверка зависимостей
./gradlew :app:dependencies > dependencies.txt

# Проверка компиляции domain модулей (если есть Android типы — не скомпилируется)
./gradlew :feature:auth:domain:build
```

**Расположение**: Проверки встроены в процесс сборки, не требуют отдельных тестовых файлов

#### ✅ Тестовые утилиты (SC-017)

**FakeClock, TestDispatchers, ResultFactory, AppErrorFactory**:
- Проверка функциональности каждой утилиты
- Проверка использования в тестах

**Расположение**:
- `core/async/src/test/java/com/eastclinic/core/async/FakeClockTest.kt`
- `core/async/src/test/java/com/eastclinic/core/async/TestDispatchersTest.kt`
- И т.д.

---

## 6. Подходы к тестированию

#### UI тесты навигации (Optional)

**NavigationSmokeTest**:
- Минимальный smoke test: старт приложения + 1-2 перехода + back navigation
- Использование testTag для устойчивости

**Расположение**:
- `app/src/androidTest/java/com/eastclinic/app/navigation/NavigationSmokeTest.kt`

**Требование**: Запущенный эмулятор или устройство

**Статус**: Опционально для Step 0, можно отложить до реальных фич

---

### 6.1 Unit тесты — изоляция и моки

**Принцип**: Каждый компонент тестируется изолированно.

**Подход**:
- Использование MockK для моков зависимостей
- Использование TestDispatchers для контролируемого выполнения корутин
- Использование FakeClock для контролируемого времени

**Пример**:
```kotlin
@Test
fun `repository returns success result`() = runTest {
    // Given - изолированная настройка
    val repository = mockk<AuthRepository>()
    coEvery { repository.login("user", "pass") } returns Result.Success(User("1", "User"))
    
    // When - тестируемое действие
    val result = repository.login("user", "pass")
    
    // Then - проверка результата
    assertTrue(result is Result.Success)
}
```

---

### 6.2 Build-Time Checks — проверка архитектурных границ

**Принцип**: Проверка соблюдения архитектурных правил на этапе сборки через Gradle и компилятор.

**Подход**:
- Domain модули используют `kotlin("jvm")` → Android классы физически недоступны
- Запрет зависимостей через Gradle dependency constraints
- Проверка графа зависимостей через `./gradlew :app:dependencies`

**Пример настройки**:
```kotlin
// В build.gradle.kts domain модуля
plugins {
    kotlin("jvm")  // НЕ com.android.library
}

dependencies {
    // Только core:common, никаких Android/Retrofit/Room
    implementation(project(":core:common"))
}
```

**Проверка**:
```bash
# Если domain попытается использовать Android — не скомпилируется
./gradlew :feature:auth:domain:build

# Проверка зависимостей
./gradlew :app:dependencies | grep -i "android\|retrofit\|room"
```

---

### 6.3 UI тесты — проверка взаимодействия (Optional)

**Принцип**: Тестирование UI на реальном устройстве/эмуляторе (опционально для Step 0).

**Подход**:
- Использование Compose Testing для Compose компонентов
- Минимальный smoke test (1-2 перехода)
- Использование testTag для устойчивости

**Пример (устойчивый)**:
```kotlin
@Test
fun `navigation smoke test`() {
    composeTestRule.setContent { RootNavGraph() }
    
    // Используем testTag вместо текста
    composeTestRule.onNodeWithTag("login_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("login_button").performClick()
    composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()
}
```

**Важно**: UI тесты опциональны для Step 0, можно отложить до реальных фич.

---

## 7. Coroutines Testing (kotlinx-coroutines-test)

### 7.1 Стандарт для тестирования корутин

**Библиотека**: `kotlinx-coroutines-test`

**Основные компоненты**:
- `runTest` — замена `runBlocking` для тестов
- `StandardTestDispatcher` — контролируемое выполнение корутин
- `UnconfinedTestDispatcher` — немедленное выполнение корутин
- `Dispatchers.setMain()` — замена Main dispatcher для тестов

### 7.2 Использование в ViewModel тестах

```kotlin
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle

@Test
fun `viewModel updates state correctly`() = runTest {
    val testDispatcher = StandardTestDispatcher()
    val viewModel = MyViewModel(repository, DispatcherProvider(testDispatcher))
    
    viewModel.handleEvent(Event.Load)
    testDispatcher.scheduler.advanceUntilIdle() // Запускаем все корутины
    
    assertEquals(expected, viewModel.uiState.value)
}
```

### 7.3 Контроль Main Dispatcher

Если ViewModel использует `Dispatchers.Main`, нужно заменить его в тестах:

```kotlin
import kotlinx.coroutines.test.setMain

@Before
fun setup() {
    Dispatchers.setMain(StandardTestDispatcher())
}

@After
fun tearDown() {
    Dispatchers.resetMain()
}
```

---

## 8. Обработка ошибок в тестах

### 8.1 Тестирование Flow с ошибками

**Паттерн**: Использование `catch` в Flow и проверка эмиссии ошибок.

```kotlin
@Test
fun `flow emits error when exception occurs`() = runTest {
    // Given
    val repository = mockk<UserRepository>()
    every { repository.observeUsers() } returns flow {
        throw IOException("Network error")
    }.catch { emit(Result.Error(AppError.NetworkError(it.message ?: ""))) }
    
    // When & Then
    repository.observeUsers().test {
        val result = awaitItem()
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).error is AppError.NetworkError)
    }
}
```

---

### 8.2 Тестирование маппинга ошибок

**Паттерн**: Проверка правильности преобразования NetworkError → AppError.

```kotlin
@Test
fun `NetworkError maps to AppError correctly`() {
    val networkError = NetworkError("Failed", 500)
    val appError = networkError.toAppError()
    
    assertTrue(appError is AppError.NetworkError)
    assertEquals("Failed", appError.message)
}
```

---

## 9. Критерии успеха тестирования (из spec.md)

### SC-014: Unit тесты для базовых типов
- ✅ AppError и Result<T> имеют покрытие всех sealed class variants
- ✅ Все тесты проходят

### SC-015: Build-Time Checks
- ✅ Проверка структуры модулей через компиляцию (domain = JVM модули)
- ✅ Проверка зависимостей через Gradle (нет недопустимых зависимостей)
- ✅ Проект компилируется без ошибок

### SC-016: Пример теста ViewModel
- ✅ Есть хотя бы один пример теста ViewModel
- ✅ Тест демонстрирует паттерн тестирования UiState/UiEvent/UiEffect
- ✅ Тест проходит

### SC-017: Тестовые утилиты
- ✅ FakeClock, TestDispatchers, ResultFactory, AppErrorFactory доступны
- ✅ Утилиты используются в тестах
- ✅ Утилиты работают корректно

---

## 10. Команды для запуска тестов

### Все тесты
```bash
./gradlew test                    # Unit тесты
./gradlew connectedAndroidTest    # UI тесты (требует устройство/эмулятор)
```

### Тесты конкретного модуля
```bash
./gradlew :core:common:test
./gradlew :feature:auth:presentation:test
./gradlew :app:test
```

### Тесты с отчетом
```bash
./gradlew test --tests "*Test" --info
```

---

## 11. Структура тестовых файлов

```
core/common/
└── src/test/java/com/eastclinic/core/common/
    ├── ResultTest.kt
    ├── AppErrorTest.kt
    ├── ResultFactory.kt          # Test utility
    └── AppErrorFactory.kt        # Test utility

core/async/
└── src/test/java/com/eastclinic/core/async/
    ├── FakeClock.kt              # Test utility
    ├── FakeClockTest.kt
    ├── TestDispatchers.kt        # Test utility
    └── TestDispatchersTest.kt

core/network/
└── src/test/java/com/eastclinic/core/network/
    └── NetworkErrorMapperTest.kt

feature/auth/presentation/
└── src/test/java/com/eastclinic/auth/presentation/login/
    └── LoginViewModelTest.kt

app/
└── src/androidTest/java/com/eastclinic/app/
    └── navigation/
        └── NavigationSmokeTest.kt  # Optional для Step 0
```

---

## 12. Best Practices

### 12.1 Именование тестов

**Паттерн**: `when [condition], then [expected result]`

```kotlin
@Test
fun `when LoadUser event, then state updates correctly`() { }

@Test
fun `when network error occurs, then error state is set`() { }
```

### 12.2 Структура теста (AAA Pattern)

```kotlin
@Test
fun testExample() {
    // Arrange (Given) - настройка
    val repository = mockk<Repository>()
    
    // Act (When) - действие
    val result = repository.getData()
    
    // Assert (Then) - проверка
    assertEquals(expected, result)
}
```

### 12.3 Изоляция тестов

- Каждый тест независим
- Не используйте общее состояние между тестами
- Используйте `@BeforeEach` для настройки, если нужно

### 12.4 Моки и стабы

- Используйте MockK для моков
- Используйте реальные объекты, когда возможно (предпочитайте стабы мокам)
- Мокайте только внешние зависимости (репозитории, API)

---

## 13. Что НЕ тестируется на Step 0

- Полное покрытие всех модулей (только примеры и базовые типы)
- End-to-end тесты реальных сценариев (только stub экраны)
- Performance тесты
- Security тесты (кроме базовых проверок архитектуры)
- Тесты реальных API интеграций (backend не интегрирован)

---

## 14. Следующие шаги после Step 0

После завершения Step 0, при реализации реальных фич:

1. **Расширение unit тестов**:
   - Тесты для всех use cases
   - Тесты для всех репозиториев
   - Тесты для всех мапперов

2. **Расширение UI тестов**:
   - Тесты для всех экранов
   - Тесты для всех пользовательских сценариев

3. **Добавление интеграционных тестов**:
   - Тесты интеграции с реальным backend (mock server)
   - Тесты интеграции с локальным хранилищем (когда появится)

4. **Добавление E2E тестов**:
   - Полные пользовательские сценарии
   - Тесты критических путей (critical paths)

---

## Резюме

### Must-Have для Step 0 (обязательно)

**На Step 0 тестирование фокусируется на**:
1. ✅ **Базовых типах** (Result, AppError) — покрытие всех sealed class variants
2. ✅ **Маппинге ошибок** (NetworkError → AppError)
3. ✅ **Тестовых утилитах** (FakeClock, TestDispatchers на основе kotlinx-coroutines-test, фабрики)
4. ✅ **Примере теста ViewModel** (демонстрация паттерна UiState/UiEvent/UiEffect)
5. ✅ **Build-time проверках** архитектурных правил (через Gradle и компиляцию)

**Используемые подходы**:
- Unit тесты с изоляцией и моками (MockK, JUnit 4)
- Тестирование корутин через `kotlinx-coroutines-test` (StandardTestDispatcher, runTest)
- Тестирование Flow через Turbine
- Build-time проверки через Gradle (domain = JVM модули, dependency rules)
- CI: `./gradlew build` и `./gradlew test`

### Optional для Step 0 (можно отложить)

- UI тесты навигации (1 smoke test с testTag)
- Robolectric (добавить позже при необходимости)
- Runtime интеграционные тесты зависимостей (лучше build checks)

**Цель**: Установить паттерны тестирования и инфраструктуру для будущей разработки фич, сохраняя простоту и стабильность для Step 0.

### Технологический стек (Must-Have)

- **JUnit 4** — для всех тестов (стандарт Android)
- **MockK** — для моков
- **Turbine** — для тестирования Flow
- **kotlinx-coroutines-test** — для тестирования корутин
- **Compose Testing** (опционально) — для UI тестов

### Target Platform

**minSdk**: 26 (Android 8.0) — для современного Compose MVP  
**targetSdk**: Последняя стабильная версия

**Важно**: minSdk должен быть синхронизирован во всех документах (plan.md, build.gradle.kts, testing strategy).

