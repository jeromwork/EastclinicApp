# Spec — Step 1: Project foundation / App skeleton

**ID**: 001-project-foundation  
**Branch**: `001-project-foundation`  
**Depends on**: Step 0 (Contract skeleton / Constitution + Clarifications)  
**Date**: 2025-01-27

## 1) Goal
Собрать минимальный, но правильный “скелет” Android-приложения (Kotlin + Jetpack Compose) с многомодульной структурой, эталонной фичей Home, рабочей навигацией, Hilt, минимальными smoke-тестами и зелёной сборкой локально и в CI.

## 2) Non-Goals (Out of scope)
- Реальная интеграция с backend API.
- Авторизация/ЕСИА, пуши, чат, платежи, локальная БД, offline-first, WebSockets.
- Сложные quality-инструменты (detekt/ktlint) — позже.
- Обязательные “фреймворк-уровня” базовые классы (BaseViewModel для всех) — не вводим.

## 3) Constraints (must follow)
- Конституция: presentation/domain/data, чистый domain, feature isolation.
- `feature:*` не импортируют другие `feature:*`.
- Зависимости: presentation → domain, data → domain; обратные запрет.
- UI/presentation не знают DTO и не ходят в сеть.
- Все версии/зависимости — только через `gradle/libs.versions.toml`.
- Каждая итерация держит билд зелёным.
- Постоянное упрощение: ищем более короткое и ясное решение перед добавлением кода; используем стандартные Jetpack/Kotlin библиотеки, если нет препятствий.

## 4) Technical decisions (Step 1)
### 4.1 Platform / SDK
- minSdk = 24
- target/compileSdk — единый источник в общих Gradle настройках (конкретные значения задаются в реализации; принцип: один источник истины).

### 4.2 UI
- Jetpack Compose (BOM), Compose Navigation.
- Контракт UI: UiState / UiEvent / UiEffect (эффекты через SharedFlow, обработка в Compose через LaunchedEffect).

### 4.3 DI
- Hilt подключаем сразу.
- Hilt bindings для фичи — в `feature:<x>:data`, не в `app` и не в отдельном `core:di`.

### 4.4 Coroutines / Time
- `core:async` включаем в Step 1: DispatcherProvider, Clock, тестовые реализации (TestDispatchers, FakeClock) для контролируемого тестирования.

## 5) Module set (минимум на Step 1)
- `:app` — композиция DI, Root navigation, стартовый экран.
- Core:
  - `:core:common` — Result, AppError, базовые чистые типы.
  - `:core:async` — DispatcherProvider, Clock, тестовые реализации.
  - `:core:ui` — theme/typography, базовый scaffold, UiEffect, AppDestination/эквивалент, общие UI-утилиты.
- Feature (reference):
  - `:feature:home:domain`
  - `:feature:home:data`
  - `:feature:home:presentation`

На Step 1 не создаём другие фичи (auth/clinics/…): только Home как эталон.

## 6) Dependency rules (кратко)
- `app` → `core:*`, `feature:home:presentation`.
- `feature:home:presentation` → `feature:home:domain`, `core:ui`, `core:common`, `core:async`.
- `feature:home:data` → `feature:home:domain` (+ опционально только core-утилиты; без сети/Retrofit).
- `feature:home:domain` → только `core:common` (и при необходимости интерфейсы из `core:async`, но держать домен максимально чистым).

## 7) Scope details (что делаем)
### 7.1 Gradle foundation
- `libs.versions.toml`: Kotlin, AGP, Compose BOM, Navigation Compose, Hilt, Coroutines, test libs (MockK, Turbine, kotlinx-coroutines-test), compose ui test, Robolectric.
- `settings.gradle.kts`: все модули зарегистрированы.
- Root build: единые SDK/Java версии (JDK 17), зелёная сборка.

### 7.2 core:common
- `Result<T>`: sealed Success/Failure.
- `AppError`: sealed базовых категорий (минимально, без сетевой привязки).

### 7.3 core:async
- `DispatcherProvider` (IO/Default/Main), `Clock` (минимальная сигнатура).
- Тестовые реализации: `TestDispatchers`, `FakeClock` (в test источниках).

### 7.4 core:ui
- Theme (минимум colors/typography/spacing).
- Базовый scaffold/каркас экрана.
- `UiEffect`: минимум Navigate(...), ShowMessage(...).
- `AppDestination` или эквивалент — централизованный тип маршрутов.

### 7.5 feature:home (референс)
- domain: `HomeRepository` (интерфейс), опциональный `GetGreetingUseCase` (только если полезен для демонстрации потока).
- data: `HomeRepositoryImpl` (stub), Hilt module @Binds `HomeRepositoryImpl -> HomeRepository`.
- presentation: `HomeScreen`, `HomeViewModel`, `HomeUiState/HomeUiEvent/HomeUiEffect`, `homeGraph()` + routes (Splash, Home, Settings).

### 7.6 Navigation composition
- В `app`: RootNavGraph + композиция `homeGraph()`.
- Навигация: старт с Splash (из home-графа) → Home → Settings.

## 8) Testing (smoke, без перегруза)
**Must-have**
- Unit: тесты на `Result` / `AppError` (базовые типы).
- Unit: тест на `HomeViewModel` или use-case с `kotlinx-coroutines-test` (и при необходимости Turbine).
- Сборка модулей: `./gradlew build test`.

**Optional**
- Compose UI smoke test на Home (по testTag, не по тексту).
- `connectedAndroidTest` — только если стабильно; иначе оставить локально.

## 9) CI
- GitHub Actions: checkout, JDK 17, Gradle cache, `./gradlew build test`.
- UI-тесты в CI — опциональны и включаются только при стабильности среды (зафиксировать в README, если пропускаем).

## 10) Documentation
- README: структура модулей, правила зависимостей (allowed/forbidden), как добавить новую фичу (3 модуля + зависимости + Hilt binding в data + subgraph в presentation), когда делить фичу на подмодули.

## 11) Definition of Done (DoD)
- `./gradlew build test` проходит локально.
- CI “build/test” зелёный.
- Приложение запускается и показывает стартовый экран.
- Навигация внутри Home-графа работает (Splash -> Home -> Settings).
- Hilt инжектит stub-репозиторий в HomeViewModel.
- Соблюдены правила зависимостей и feature isolation.
- Есть минимум один unit-тест для ViewModel/use-case и тесты базовых типов.

