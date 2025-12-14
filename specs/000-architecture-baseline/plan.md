# Implementation Plan: Step 0 — Contract skeleton (Architecture baseline)

**Branch**: `000-architecture-baseline` | **Date**: 2025-01-27 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/000-architecture-baseline/spec.md`

## Summary

Создание архитектурного каркаса Android-приложения клиники на Kotlin + Jetpack Compose с соблюдением Clean Architecture. Цель: довести репозиторий до состояния "зелёная сборка" с рабочей навигацией между stub-экранами, готовностью к подключению реальных фич без пересборки архитектуры.

**Технический подход**: Multi-module структура с разделением на core-модули (common, async, ui, network, auth-contract, push-contract) и feature-модули (каждый из 3 модулей: presentation, domain, data). Использование Hilt для DI, Compose Navigation для навигации, строгие границы зависимостей через Gradle.

## Technical Context

**Language/Version**: Kotlin 1.9+  
**Primary Dependencies**: 
- Android Gradle Plugin 8.1+
- Jetpack Compose (Compose BOM)
- Hilt (Dependency Injection)
- Compose Navigation
- Retrofit/OkHttp (в core:network, без утечки типов)
- Coroutines + Flow

**Storage**: Не используется на Step 0 (core:database отложен)  
**Testing**: 
- JUnit 5 для unit тестов
- MockK для моков
- Turbine для тестирования Flow
- Robolectric для Android-тестов
- Compose Testing (compose-ui-test-junit4) для UI тестов
- Espresso (опционально) для интеграционных UI тестов
**Target Platform**: Android API 26+ (Android 8.0+) — для современного Compose MVP  
**Project Type**: Mobile (Android native)  
**Performance Goals**: 
- Сборка проекта < 2 минут на стандартной машине
- Запуск приложения < 5 секунд после сборки
- Навигация между экранами без задержек

**Constraints**: 
- Domain слой без Android/Retrofit/Room типов
- Feature модули изолированы (не зависят друг от друга)
- Core модули — ацикличный граф зависимостей
- Запрет: core:auth → core:network

**Scale/Scope**: 
- 5-6 feature модулей (auth, home, clinics, doctors, appointments, chat) как skeleton
- Каждый feature = 3 модуля (presentation, domain, data)
- Всего ~20-25 модулей включая core и app

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### ✅ Architecture is non-negotiable
- [x] Каждый feature разделён на presentation/domain/data слои
- [x] UI не вызывает сеть напрямую и не знает DTO
- [x] Domain слой чистый (без Android/Retrofit/Room типов)
- [x] Зависимости только внутрь: presentation → domain, data → domain
- [x] Feature изоляция: feature:* не импортирует другие feature:*
- [x] Общий код только через core:*

### ✅ Risk zones behind interfaces
- [x] Auth скрыт за интерфейсами (AuthRepository, SessionStore, TokenRefresher)
- [x] Push скрыт за интерфейсами (PushTokenProvider, NotificationHandler)
- [x] Network скрыт за интерфейсами (без утечки Retrofit типов)
- [x] Нет SDK типов в ViewModel/use-cases/UiState

### ✅ UI contract standard (UDF)
- [x] ViewModel экспонирует UiState через StateFlow
- [x] ViewModel принимает UiEvent
- [x] ViewModel эмитит UiEffect для одноразовых событий
- [x] UiState/UiModel не содержат DTO/entity типов

### ✅ MVP constraints
- [x] Нет offline-first сложности
- [x] Нет KMM
- [x] Нет WebSockets (только контракты если нужно)
- [x] Минимальные deep links (только hooks)

### ✅ Security defaults
- [x] Токены только в encrypted storage
- [x] Нет логирования токенов/PII/PHI
- [x] Маскирование идентификаторов в логах

**Status**: ✅ Все проверки пройдены. Архитектура соответствует конституции.

## Project Structure

### Documentation (this feature)

```text
specs/000-architecture-baseline/
├── plan.md              # This file (/speckit.plan command output)
├── spec.md              # Feature specification
├── CLARIFICATIONS.md    # Architectural decisions
├── checklists/
│   └── requirements.md  # Quality checklist
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
EastclinicApp/
├── app/                          # Application module (composition layer)
│   ├── src/main/java/com/eastclinic/app/
│   │   ├── EastclinicApplication.kt
│   │   ├── di/
│   │   │   └── AppModule.kt
│   │   └── navigation/
│   │       └── RootNavGraph.kt
│   └── build.gradle.kts
│
├── core/
│   ├── common/                   # Core: Common (Result, AppError, базовые типы)
│   │   ├── src/main/java/com/eastclinic/core/common/
│   │   │   ├── Result.kt
│   │   │   ├── AppError.kt
│   │   │   └── ...
│   │   └── build.gradle.kts
│   │
│   ├── async/                    # Core: Async (DispatcherProvider, Clock)
│   │   ├── src/main/java/com/eastclinic/core/async/
│   │   │   ├── DispatcherProvider.kt
│   │   │   ├── Clock.kt
│   │   │   └── ...
│   │   ├── src/test/java/...     # FakeClock, TestDispatchers
│   │   └── build.gradle.kts
│   │
│   ├── ui/                       # Core: UI (Theme, UiEffect, навигация)
│   │   ├── src/main/java/com/eastclinic/core/ui/
│   │   │   ├── theme/
│   │   │   ├── UiEffect.kt
│   │   │   ├── AppDestination.kt
│   │   │   └── ...
│   │   └── build.gradle.kts
│   │
│   ├── network/                  # Core: Network (Retrofit wiring, safeCall)
│   │   ├── src/main/java/com/eastclinic/core/network/
│   │   │   ├── NetworkResult.kt
│   │   │   ├── NetworkError.kt
│   │   │   ├── safeCall.kt
│   │   │   └── ...
│   │   └── build.gradle.kts
│   │
│   ├── auth-contract/            # Core: Auth Contract (интерфейсы)
│   │   ├── src/main/java/com/eastclinic/core/auth/
│   │   │   ├── AuthTokenProvider.kt
│   │   │   ├── SessionStore.kt
│   │   │   └── ...
│   │   └── build.gradle.kts
│   │
│   └── push-contract/            # Core: Push Contract (интерфейсы)
│       ├── src/main/java/com/eastclinic/core/push/
│       │   ├── PushTokenProvider.kt
│       │   ├── NotificationHandler.kt
│       │   └── ...
│       └── build.gradle.kts
│
├── feature/
│   ├── auth/
│   │   ├── presentation/         # Feature: Auth Presentation
│   │   │   ├── src/main/java/com/eastclinic/auth/presentation/
│   │   │   │   ├── login/
│   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   ├── LoginViewModel.kt
│   │   │   │   │   ├── LoginUiState.kt
│   │   │   │   │   ├── LoginUiEvent.kt
│   │   │   │   │   └── LoginUiEffect.kt
│   │   │   │   └── navigation/
│   │   │   │       ├── AuthRoutes.kt
│   │   │   │       └── authGraph.kt
│   │   │   └── build.gradle.kts
│   │   │
│   │   ├── domain/               # Feature: Auth Domain
│   │   │   ├── src/main/java/com/eastclinic/auth/domain/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   │   └── AuthRepository.kt
│   │   │   │   └── usecase/
│   │   │   └── build.gradle.kts
│   │   │
│   │   └── data/                 # Feature: Auth Data
│   │       ├── src/main/java/com/eastclinic/auth/data/
│   │       │   ├── repository/
│   │       │   │   └── AuthRepositoryImpl.kt
│   │       │   ├── di/
│   │       │   │   └── AuthDataModule.kt
│   │       │   └── ...
│   │       └── build.gradle.kts
│   │
│   ├── home/                     # Feature: Home (контейнерный экран)
│   │   ├── presentation/
│   │   ├── domain/
│   │   └── data/
│   │
│   ├── clinics/                  # Feature: Clinics
│   │   ├── presentation/
│   │   ├── domain/
│   │   └── data/
│   │
│   ├── doctors/                  # Feature: Doctors
│   │   ├── presentation/
│   │   ├── domain/
│   │   └── data/
│   │
│   ├── appointments/             # Feature: Appointments
│   │   ├── presentation/
│   │   ├── domain/
│   │   └── data/
│   │
│   └── chat/                     # Feature: Chat
│       ├── presentation/
│       ├── domain/
│       └── data/
│
├── build.gradle.kts              # Root build file
├── settings.gradle.kts           # Module registration
├── gradle.properties
└── gradle/
    └── libs.versions.toml        # Version catalog
```

**Structure Decision**: Multi-module Android проект с разделением на core-модули (инфраструктура) и feature-модули (бизнес-логика). Каждый feature состоит из 3 модулей для строгого разделения слоёв. app модуль — композиционный слой для навигации и DI.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A | Все решения соответствуют конституции | - |

## Phase 0: Research & Decisions

**Цель**: Убедиться, что все технические решения приняты и задокументированы.

### Research Tasks

Все ключевые решения уже приняты в секции Clarifications/Decisions в spec.md:

1. ✅ **Модули и границы** — решено: 3 модуля на feature, размещение базовых типов
2. ✅ **Навигация** — решено: Compose Navigation, подграфы в feature, композиция в app
3. ✅ **DI** — решено: Hilt, модули в каждом модуле
4. ✅ **Network** — решено: практичный контракт, safeCall, маппинг ошибок
5. ✅ **UI паттерн** — решено: UiState/UiEvent/UiEffect, SharedFlow + LaunchedEffect
6. ✅ **Тестирование** — решено: unit тесты, интеграционные тесты, test fixtures

**Output**: Все решения задокументированы в spec.md, секция "Clarifications / Decisions". Дополнительного research.md не требуется.

## Phase 1: Design & Contracts

### 1.1 Data Model

**Файл**: `specs/000-architecture-baseline/data-model.md`

На Step 0 реальных доменных моделей нет (только skeleton). Основные типы:

- **AppError** (sealed class в core:common)
  - NetworkError
  - ValidationError
  - UnknownError
  - и другие базовые типы

- **Result<T>** (sealed class в core:common)
  - Success<T>(data: T)
  - Error(error: AppError)

- **DispatcherProvider** (интерфейс в core:async)
- **Clock** (интерфейс в core:async)

- **UiEffect** (sealed class в core:ui)
  - Navigate(route: String)
  - ShowMessage(message: String)

Детальные доменные модели будут созданы при реализации конкретных фич.

### 1.2 API Contracts

**Директория**: `specs/000-architecture-baseline/contracts/`

На Step 0 реальные API контракты не определяются (backend уже существует, интеграция будет позже). 

Создаются только интерфейсы в core:network:
- `NetworkResult<T>` — результат сетевого запроса
- `NetworkError` — ошибка сети (маппится в AppError)
- `safeCall()` — функция-обёртка для безопасных сетевых вызовов

### 1.3 Quickstart Guide

**Файл**: `specs/000-architecture-baseline/quickstart.md`

Создаётся после завершения Phase 1 с инструкциями по:
- Настройке окружения
- Сборке проекта
- Запуску приложения
- Добавлению нового feature модуля

## Phase 2: Implementation Steps

### Step 1: Bootstrap Gradle Project

**Цель**: Создать базовую структуру Gradle проекта с version catalog и настройками.

**Файлы**:
- `settings.gradle.kts` — регистрация модулей
- `build.gradle.kts` (root) — общие настройки
- `gradle/libs.versions.toml` — version catalog (централизованное управление версиями)
- `gradle.properties` — свойства сборки

**Важно**: Все зависимости и версии должны управляться через `libs.versions.toml`:
- Версии Kotlin, Android Gradle Plugin
- Версии библиотек (Hilt, Compose BOM, Navigation, Retrofit, OkHttp, Coroutines)
- Версии тестовых библиотек (JUnit, MockK, Turbine, Compose Testing)

**Проверка**: `./gradlew tasks` выполняется без ошибок, все версии задекларированы в version catalog

### Step 2: Create Core Modules

**Цель**: Создать все core-модули с базовыми типами и контрактами.

**Порядок создания** (важен для зависимостей):
1. `core:common` — Result, AppError
2. `core:async` — DispatcherProvider, Clock (зависит от core:common)
3. `core:ui` — Theme, UiEffect (зависит от core:common)
4. `core:auth-contract` — интерфейсы auth (зависит от core:common)
5. `core:push-contract` — интерфейсы push (зависит от core:common)
6. `core:network` — NetworkResult, safeCall (зависит от core:common, core:auth-contract)

**Проверка**: `./gradlew :core:common:build` и аналогично для всех core модулей

### Step 3: Create Test Infrastructure

**Цель**: Создать тестовые утилиты и написать unit тесты для базовых типов.

**Файлы**:
- `core:async/src/test/java/.../FakeClock.kt`
- `core:async/src/test/java/.../TestDispatchers.kt`
- `core:common/src/test/java/.../ResultTest.kt`
- `core:common/src/test/java/.../AppErrorTest.kt`

**Проверка**: `./gradlew test` проходит успешно

### Step 4: Setup Hilt

**Цель**: Подключить Hilt в app модуле и создать пример DI модуля.

**Файлы**:
- `app/build.gradle.kts` — добавление Hilt плагинов
- `app/src/main/java/.../EastclinicApplication.kt` — @HiltAndroidApp
- Пример DI модуля в одном из feature:data

**Проверка**: `./gradlew :app:assembleDebug` собирается

### Step 5: Create Feature Skeleton Modules

**Цель**: Создать skeleton для всех feature модулей (auth, home, clinics, doctors, appointments, chat).

**Для каждого feature**:
1. Создать `feature:<x>:domain` — интерфейс Repository, доменная модель (stub)
2. Создать `feature:<x>:data` — RepositoryImpl (stub), Hilt модуль
3. Создать `feature:<x>:presentation` — Screen (stub), ViewModel (stub), UiState/Event/Effect, routes

**Проверка**: Каждый модуль компилируется отдельно

### Step 6: Setup Compose Navigation

**Цель**: Настроить Compose Navigation с подграфами в feature модулях и композицией в app.

**Файлы**:
- `app/src/main/java/.../navigation/RootNavGraph.kt` — корневой граф
- В каждом `feature:<x>:presentation` — функция `NavGraphBuilder.<feature>Graph()`
- Обработка `UiEffect.Navigate` в app модуле

**Проверка**: Приложение запускается, навигация между stub экранами работает

### Step 7: Implement Network Skeleton

**Цель**: Реализовать каркас core:network с safeCall и маппингом ошибок.

**Файлы**:
- `core:network/src/main/java/.../NetworkResult.kt`
- `core:network/src/main/java/.../safeCall.kt`
- Интерцепторы (logging, headers, auth placeholder)

**Проверка**: Unit тест маппинга ошибок проходит

### Step 8: Final Quality Check

**Цель**: Убедиться, что всё собирается и работает.

**Проверки**:
- `./gradlew build` — зелёная сборка
- `./gradlew test` — все unit тесты проходят
- `./gradlew connectedAndroidTest` — UI тесты проходят (если добавлены)
- Приложение запускается и навигация работает
- Зависимости не нарушают правила:
  - `./gradlew :app:dependencies` — проверка графа зависимостей
  - Проверка отсутствия циклических зависимостей
  - Проверка отсутствия Android/Retrofit/Room типов в domain модулях
  - Проверка отсутствия зависимости core:auth → core:network

## Testing Strategy

### Unit Tests
- Базовые типы (Result, AppError) — 100% покрытие sealed class variants
- Тестовые утилиты (FakeClock, TestDispatchers) — проверка функциональности
- ViewModel — пример теста для демонстрации паттерна

### Integration Tests
- Проверка структуры модулей и зависимостей
- Проверка отсутствия нарушений архитектурных правил
- Проверка ацикличности графа зависимостей core модулей

### UI Tests
- Минимальные UI тесты для проверки навигации между stub экранами
- Тесты отображения простых UI элементов
- Использование Compose Testing для тестирования Compose компонентов

### Flow Error Handling
Все Flow операции в data слое должны использовать `catch` для обработки ошибок:
```kotlin
flow { ... }
    .catch { exception ->
        emit(Result.Error(mapToAppError(exception)))
    }
```

## Out of Scope for Step 0

Следующие требования фиксируются, но реализация откладывается:

1. **Эквайринг банка** — только точки расширения/интерфейсы
2. **ЕСИА/OAuth2/OIDC интеграция** — только контракты в core:auth-contract
3. **Реальные push через FCM** — только контракты в core:push-contract
4. **Реальный чат/медиа** — только каркас модулей feature:chat
5. **core:database** — отложен до первой реальной локальной сущности

## Success Criteria Validation

После завершения всех шагов должны быть выполнены:

- ✅ SC-001: Все модули компилируются без ошибок
- ✅ SC-002: Приложение запускается < 5 секунд
- ✅ SC-003: Root screen отображается без крашей
- ✅ SC-004: Навигация между stub экранами работает
- ✅ SC-005: Domain слой не содержит Android/Retrofit/Room типов
- ✅ SC-006: Базовые типы доступны из feature модулей
- ✅ SC-007: Хотя бы один экран демонстрирует UiState/UiEvent/UiEffect
- ✅ SC-008: Правила зависимостей соблюдены
- ✅ SC-009: Feature изоляция соблюдена
- ✅ SC-010: Каждый feature = 3 модуля
- ✅ SC-011: Core модули — ацикличный граф
- ✅ SC-012: Hilt модули настроены
- ✅ SC-013: Навигационные подграфы созданы
- ✅ SC-014: Unit тесты для базовых типов есть
- ✅ SC-015: Интеграционные тесты проверяют структуру
- ✅ SC-016: Пример теста ViewModel есть
- ✅ SC-017: Тестовые утилиты доступны
- ✅ SC-018: Сборка < 2 минут

## Next Steps

После завершения Step 0:

1. Запустить `/speckit.tasks` для создания детального tasks.md
2. Начать реализацию по tasks.md
3. После завершения — перейти к реализации реальных фич

