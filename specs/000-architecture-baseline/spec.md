# Feature Specification: Step 0 — Contract skeleton (Architecture baseline)

**Feature Branch**: `000-architecture-baseline`  
**Created**: 2025-01-27  
**Status**: Draft  
**Input**: User description: "Step 0 — Contract skeleton (Architecture baseline). Create multi-module baseline: app, core:common, core:ui, core:network, core:auth, core:push, optional core:database, plus feature:* modules. Establish strict Clean Architecture boundaries: presentation/domain/data per feature. Define dependency rules (presentation -> domain, data -> domain; feature isolation; shared only via core:*), aligned with the constitution. Add base types: AppError, Result<T>, DispatcherProvider, Clock. Define UI architecture style: UiState + UiEvent + UiEffect (unidirectional flow). Deliver 'Hello MVP': app builds green and launches; stub screens wired with end-to-end navigation."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Application Launches Successfully (Priority: P1)

As a developer, I need the application to build and launch successfully so that I can verify the architecture foundation is in place and begin feature development.

**Why this priority**: This is the foundational requirement - without a working build and launch, no further development can proceed. This validates that all module dependencies are correctly configured and the basic app structure is sound.

**Independent Test**: Can be fully tested by running the build command and launching the app on a device or emulator. The app should display a simple root screen and demonstrate that the module structure is functional.

**Acceptance Scenarios**:

1. **Given** the project is cloned and dependencies are installed, **When** the build command is executed, **Then** the build completes successfully with no compilation errors
2. **Given** the app is built successfully, **When** the app is launched, **Then** it displays a root screen without crashing
3. **Given** the app is running, **When** navigation actions are triggered, **Then** the app navigates between stub screens without errors

---

### User Story 2 - Module Structure and Boundaries Established (Priority: P1)

As a developer, I need clearly defined module boundaries and dependency rules so that I can maintain clean architecture principles and prevent architectural violations.

**Why this priority**: The module structure and dependency rules are the foundation of the entire architecture. Without these boundaries, the codebase will quickly degrade and violate Clean Architecture principles, making future development difficult.

**Independent Test**: Can be fully tested by verifying that all required modules exist, compile independently, and that dependency rules are enforced (e.g., domain modules have no Android dependencies, presentation modules depend only on domain, not data).

**Acceptance Scenarios**:

1. **Given** the project structure is examined, **When** module dependencies are analyzed, **Then** all core modules (app, core:common, core:ui, core:network, core:auth, core:push, core:async, optional core:database) exist and compile
2. **Given** a feature module is created, **When** its dependencies are checked, **Then** it consists of three separate modules (feature:<x>:presentation, feature:<x>:domain, feature:<x>:data) with correct layer structure
3. **Given** domain layer code is examined, **When** imports are analyzed, **Then** no Android, Retrofit, or Room types are present in domain modules
4. **Given** core module dependencies are analyzed, **When** dependency graph is checked, **Then** it is acyclic with no core:auth → core:network dependency

---

### User Story 3 - Base Types and Abstractions Available (Priority: P1)

As a developer, I need base types and abstractions (AppError, Result<T>, DispatcherProvider, Clock) so that I can implement features consistently without duplicating common patterns.

**Why this priority**: These base types are fundamental building blocks that all features will use. Having them in place from the start ensures consistency and prevents rework when implementing features.

**Independent Test**: Can be fully tested by importing and using these types in a simple test or stub feature, verifying they compile and provide the expected abstractions.

**Acceptance Scenarios**:

1. **Given** a feature module is created, **When** base types are imported, **Then** AppError and Result<T> from core:common, DispatcherProvider and Clock from core:async are available and usable
2. **Given** an error occurs in a feature, **When** error handling is implemented, **Then** AppError (sealed class) can be used to represent the error consistently with type-safe error handling
3. **Given** asynchronous operations are needed, **When** they are executed, **Then** DispatcherProvider from core:async provides the necessary execution context abstractions
4. **Given** a feature needs to return operation results, **When** Result<T> is used, **Then** it provides type-safe Success/Error variants without nullable fields

---

### User Story 4 - UI Architecture Pattern Established (Priority: P1)

As a developer, I need a consistent UI architecture pattern (UiState + UiEvent + UiEffect) so that all screens follow unidirectional data flow and maintain predictable state management.

**Why this priority**: Consistent UI patterns across the app reduce cognitive load, make code easier to understand, and prevent state management bugs. This pattern must be established before any real features are built.

**Independent Test**: Can be fully tested by creating a stub screen that uses UiState, UiEvent, and UiEffect, verifying that state updates flow unidirectionally and effects are handled correctly.

**Acceptance Scenarios**:

1. **Given** a stub screen is created, **When** ViewModel is implemented, **Then** it exposes feature-specific UiState via StateFlow, accepts feature-specific UiEvent, and emits UiEffect (with common types from core:ui) for one-off events
2. **Given** a user interaction occurs, **When** an event is sent to ViewModel, **Then** state updates flow unidirectionally through the ViewModel
3. **Given** navigation or one-off actions are needed, **When** UiEffect is emitted, **Then** the UI layer handles the effect via SharedFlow subscription using LaunchedEffect in Compose
4. **Given** navigation between features is needed, **When** UiEffect.Navigate is emitted, **Then** app module handles the navigation decision, maintaining feature isolation

---

### User Story 5 - End-to-End Navigation Working (Priority: P2)

As a developer, I need working navigation between stub screens so that I can verify the navigation infrastructure is functional and ready for feature implementation.

**Why this priority**: While not as critical as the build and module structure, working navigation demonstrates that the app infrastructure is complete and ready for feature development. This validates that navigation dependencies and routing are correctly configured.

**Independent Test**: Can be fully tested by navigating between stub screens in the app, verifying that navigation actions work and screens transition correctly.

**Acceptance Scenarios**:

1. **Given** the app is launched, **When** navigation actions are triggered from the root screen, **Then** the app navigates to target stub screens using Compose Navigation
2. **Given** a stub screen is displayed, **When** back navigation is triggered, **Then** the app returns to the previous screen correctly
3. **Given** multiple stub screens exist, **When** navigation flows are tested, **Then** all navigation paths work without crashes or errors
4. **Given** feature modules define navigation subgraphs, **When** app module composes them, **Then** all feature screens are accessible in the root navigation graph
5. **Given** a feature needs to navigate to another feature, **When** UiEffect.Navigate is emitted, **Then** app module handles the navigation without creating direct feature dependencies

---

### User Story 6 - Testing Infrastructure Established (Priority: P2)

As a developer, I need testing infrastructure and patterns established so that I can write consistent, maintainable tests for features following established patterns.

**Why this priority**: Having testing patterns and utilities in place from the start ensures consistency across the codebase and speeds up test development. It also demonstrates how to test the architecture patterns (UiState/UiEvent/UiEffect, Result<T>, etc.).

**Independent Test**: Can be fully tested by writing unit tests for base types and at least one ViewModel test, verifying that test utilities work correctly and patterns are clear.

**Acceptance Scenarios**:

1. **Given** base types are implemented, **When** unit tests are written, **Then** AppError and Result<T> have 100% coverage of sealed class variants
2. **Given** test utilities are created, **When** tests use FakeClock, TestDispatchers, and result/error factories, **Then** tests are consistent and easy to write
3. **Given** a stub screen ViewModel exists, **When** a ViewModel test is written, **Then** it demonstrates testing pattern for UiState/UiEvent/UiEffect
4. **Given** module structure is established, **When** integration tests are written, **Then** they verify dependency rules and module boundaries are not violated

---

### Edge Cases

- What happens when a module dependency is incorrectly configured (e.g., domain depends on Android types, or core:auth depends on core:network)?
- How does the system handle missing base types when a new feature module is added (AppError/Result from core:common, DispatcherProvider/Clock from core:async)?
- What happens when navigation targets don't exist or are misconfigured in feature subgraphs?
- How does the build system handle optional modules (e.g., core:database) when they're not included?
- What happens when UI state updates occur during configuration changes or process death?
- What happens when a feature module tries to import another feature module directly (violating isolation)?
- How does Hilt handle circular dependencies if DI modules are incorrectly configured?
- What happens when UiEffect.Navigate is emitted but the target route doesn't exist in the composed navigation graph?
- How does the system handle test utilities when a feature module doesn't have access to core:async test fixtures?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST have a multi-module structure with app module, core:common, core:ui, core:network, core:auth, core:push, core:async (or core:coroutines), optional core:database, and support for feature:* modules
- **FR-002**: System MUST enforce Clean Architecture boundaries: each feature consists of three separate modules (feature:<x>:presentation, feature:<x>:domain, feature:<x>:data)
- **FR-003**: System MUST enforce dependency rules: presentation → domain, data → domain, with no reverse dependencies
- **FR-004**: System MUST enforce feature isolation: feature:* modules must NOT import other feature:* modules directly
- **FR-005**: System MUST allow shared code only through core:* modules, never through direct feature-to-feature dependencies
- **FR-006**: System MUST provide base type AppError (sealed class) in core:common for consistent error representation across all layers
- **FR-007**: System MUST provide base type Result<T> (sealed class with Success/Error) in core:common for handling success/failure outcomes in a type-safe manner
- **FR-008**: System MUST provide DispatcherProvider abstraction in core:async for managing asynchronous operation execution contexts
- **FR-009**: System MUST provide Clock abstraction in core:async for time-based operations
- **FR-010**: System MUST define UI architecture pattern using UiState (exposed via StateFlow), UiEvent (accepted by ViewModel), and UiEffect (emitted for one-off events)
- **FR-011**: System MUST ensure domain layer contains no Android types (no Android.* imports)
- **FR-012**: System MUST ensure domain layer contains no Retrofit types (no retrofit2.* imports)
- **FR-013**: System MUST ensure domain layer contains no Room types (no androidx.room.* imports)
- **FR-014**: System MUST use Compose Navigation (androidx.navigation.compose) for navigation
- **FR-015**: System MUST define navigation subgraphs in each feature:*:presentation module, with composition in app module
- **FR-016**: System MUST use Hilt for dependency injection with DI modules in each module
- **FR-017**: System MUST provide core:network with practical contract (safeCall/NetworkResult/NetworkError) without leaking Retrofit types
- **FR-018**: System MUST define common UiEffect types (Navigate, Message) in core:ui, while State/Event remain feature-specific
- **FR-019**: System MUST provide test utilities (FakeClock, TestDispatchers, result/error factories) for consistent testing
- **FR-020**: System MUST enforce acyclic dependency graph between core modules, with explicit prohibition of core:auth → core:network dependency
- **FR-021**: System MUST allow app module to depend on feature:*:presentation modules for navigation registration, but not on domain/data layers
- **FR-022**: System MUST compile successfully with all modules building without errors
- **FR-023**: System MUST launch to a root screen when the app starts
- **FR-024**: System MUST support navigation between stub screens with end-to-end navigation working via UiEffect.Navigate handled by app module
- **FR-025**: System MUST align all architectural decisions with the constitution document

### Key Entities *(include if feature involves data)*

- **AppError**: Sealed class in core:common representing normalized error types (NetworkError, ValidationError, UnknownError, etc.) across the application, providing a stable interface for error handling without exposing implementation details
- **Result<T>**: Sealed class in core:common with two variants: `Result.Success<T>(data: T)` and `Result.Error(error: AppError)`, representing the outcome of an operation in a type-safe manner
- **DispatcherProvider**: Abstraction in core:async for providing coroutine dispatchers, allowing testability and flexibility in dispatcher selection
- **Clock**: Abstraction in core:async for time-based operations, enabling testability by allowing time to be controlled in tests
- **UiState**: Immutable state class (feature-specific) representing the current state of a screen, exposed via StateFlow from ViewModel
- **UiEvent**: Sealed class or interface (feature-specific) representing user actions or events that trigger state changes in ViewModel
- **UiEffect**: Sealed class in core:ui with common types (Navigate, Message) and feature-specific extensions, representing one-off events (navigation, toasts, dialogs) that don't affect state but require UI reactions
- **NetworkResult/NetworkError**: Types in core:network for network operation results, providing practical contract for API calls without leaking Retrofit types
- **Navigation Routes**: Public route constants/objects in feature:*:presentation modules, imported by app module for navigation graph composition

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: All modules compile successfully with zero compilation errors when build command is executed
- **SC-002**: Application launches successfully on target device/emulator within 5 seconds of build completion
- **SC-003**: Root screen displays without crashes or errors when app launches
- **SC-004**: Navigation between at least 3 stub screens works correctly with 100% success rate (no crashes or navigation failures) using Compose Navigation
- **SC-005**: Domain layer modules have zero dependencies on Android, Retrofit, or Room types (verified by dependency analysis)
- **SC-006**: All base types (AppError, Result<T> from core:common; DispatcherProvider, Clock from core:async) are accessible and usable from feature modules
- **SC-007**: At least one stub screen implements UiState + UiEvent + UiEffect pattern correctly, demonstrating unidirectional data flow with SharedFlow subscription via LaunchedEffect
- **SC-008**: Dependency rules are enforced: no presentation or data layer code directly depends on each other (only through domain)
- **SC-009**: Feature isolation is maintained: feature modules can be added without creating circular dependencies or violating isolation rules
- **SC-010**: Each feature consists of three separate modules (presentation, domain, data) with correct Gradle dependencies
- **SC-011**: Core modules follow acyclic dependency graph with no core:auth → core:network dependency
- **SC-012**: Hilt DI modules are defined in each module and properly integrated in app module
- **SC-013**: Navigation subgraphs are defined in feature:*:presentation modules and composed in app module
- **SC-014**: Unit tests exist for base types (AppError, Result<T>) with 100% coverage of sealed class variants
- **SC-015**: Integration tests verify module structure and dependency rules are not violated
- **SC-016**: At least one example ViewModel test demonstrates testing pattern for UiState/UiEvent/UiEffect
- **SC-017**: Test utilities (FakeClock, TestDispatchers, result/error factories) are available and used in tests
- **SC-018**: Build completes in under 2 minutes on a standard development machine for a clean build

## Assumptions

- The project uses Kotlin as the primary language
- The project uses Gradle as the build system
- The project targets Android platform
- UI framework: Jetpack Compose (based on Compose Navigation choice)
- Coroutines will be used for asynchronous operations
- Hilt is used for dependency injection
- Compose Navigation (androidx.navigation.compose) is used for navigation
- The constitution document (.specify/memory/constitution.md) is the source of truth for architectural decisions
- Optional modules (like core:database) may not be included in the initial baseline but the structure must support them
- Stub screens are minimal implementations that demonstrate navigation and architecture patterns, not full feature implementations
- Feature modules follow package structure: `com.eastclinic.<feature>.presentation`, `com.eastclinic.<feature>.domain`, `com.eastclinic.<feature>.data`

## Clarifications / Decisions

**Session**: 2025-01-27

### (1) Модули и границы

**Q1.1: Структура feature:* модулей**  
**Решение**: Каждый feature состоит из трёх отдельных модулей: `feature:<x>:presentation`, `feature:<x>:domain`, `feature:<x>:data`  
**Обоснование**: Границы слоёв закрепляются зависимостями Gradle; меньше шансов нарушить Clean Architecture "случайно".

**Q1.2: Размещение базовых типов**  
**Решение**: AppError и Result<T> размещаются в `core:common`. DispatcherProvider и Clock размещаются в отдельном модуле `core:async` (или `core:coroutines`)  
**Обоснование**: Ошибки/Result — фундамент домена и должны быть максимально "чистыми". Диспетчеры и источник времени тесно связаны с тестированием (фейки/тестовые реализации), поэтому логичнее держать рядом в async-инфраструктуре, не раздувая core:common.

**Q1.3: Зависимости между core модулями**  
**Решение**: Разрешается строго ограниченный ацикличный граф зависимостей между core модулями. Запрещается зависимость `core:auth` → `core:network`  
**Обоснование**: Полностью "только → common" часто ломается на практике, но допускать "auth зависит от network" — путь к циклам. Правильнее: core:auth-* = контракты/хранилище сессии, сеть может зависеть лишь от интерфейса токена, а не от реализации auth.

**Q1.4: Зависимости app модуля**  
**Решение**: `app` модуль зависит от `feature:*:presentation` для регистрации экранов/графов навигации  
**Обоснование**: app — композиционный модуль; он "склеивает" навигацию и DI. Domain/data фич он трогать не должен.

### (2) Навигация и графы

**Q2.1: Библиотека навигации**  
**Решение**: Compose Navigation (androidx.navigation.compose)  
**Обоснование**: Раз UI на Compose — берём нативную навигацию для Compose без дополнительной сложности.

**Q2.2: Расположение графа навигации**  
**Решение**: В каждом `feature:*:presentation` определяется свой подграф навигации. Композиция всех подграфов происходит в `app` модуле  
**Обоснование**: Фичи остаются изолированными, app только собирает их в единый root-graph.

**Q2.3: Регистрация экранов feature модулей**  
**Решение**: Через публичные route-константы/объекты, которые импортируются в `app` модуле  
**Обоснование**: Для Step 0 это самый простой, прозрачный и стабильный способ без DI-магии/рефлексии/кодгена.

**Q2.4: Навигация между feature модулями**  
**Решение**: Через `UiEffect.Navigate`, который обрабатывается `app` модулем  
**Обоснование**: Фича не знает "куда именно" в масштабе приложения — она эмитит намерение, а app принимает решение.

### (3) Dependency Injection (DI)

**Q3.1: Библиотека DI**  
**Решение**: Hilt (Google's recommended DI library for Android)  
**Обоснование**: Стандарт Android, compile-time безопасность, хорошо подходит multi-module.

**Q3.2: Размещение DI модулей**  
**Решение**: В каждом модуле — свой DI модуль (Hilt Module)  
**Обоснование**: Реализации поставляются там, где они реализованы (обычно *:data и инфраструктурные core-модули). app не становится "бог-модулем".

**Q3.3: Application класс**  
**Решение**: Application класс находится в `app` модуле  
**Обоснование**: Android entrypoint должен оставаться в app, core — без Android-зависимостей.

### (4) core:network контракт

**Q4.1: Интерфейсы/контракты core:network**  
**Решение**: Практичный контракт ближе к API-вызовам (safeCall/NetworkResult/NetworkError), без утечки Retrofit типов наружу  
**Обоснование**: Абстракция "Request/Response как свой HTTP" для Step 0 — лишняя. Нужнее единый способ вызывать сеть и маппить ошибки.

**Q4.2: Использование network в feature модулях**  
**Решение**: Domain слой определяет интерфейсы репозиториев, data слой реализует через `core:network`  
**Обоснование**: Domain остаётся чистым и не знает про сеть; data — адаптер.

### (5) Шаблон feature модуля

**Q5.1: Структура пакетов**  
**Решение**: По слоям: `com.eastclinic.<feature>.presentation`, `com.eastclinic.<feature>.domain`, `com.eastclinic.<feature>.data`  
**Обоснование**: Самая понятная структура для поддержки и для новичка.

**Q5.2: Шаблон/архетип новой фичи**  
**Решение**: Только структура папок + документация/пример. Генераторы кода не используются на Step 0  
**Обоснование**: На Step 0 архитектура ещё стабилизируется; генераторы могут закрепить неправильные решения.

### (6) Базовые типы ошибок и Result

**Q6.1: Структура AppError**  
**Решение**: Sealed class с типами ошибок (NetworkError, ValidationError, UnknownError и т.д.)  
**Обоснование**: Type-safe, exhaustiveness, удобный маппинг и тестирование.

**Q6.2: Структура Result<T>**  
**Решение**: Sealed class: `Result.Success<T>(data: T)` и `Result.Error(error: AppError)`  
**Обоснование**: Ошибка становится явной частью результата, без nullable-полей и внешних библиотек.

**Q6.3: Расширяемость ошибок**  
**Решение**: Через композицию: доменные ошибки внутри фичи, наружу — маппинг в AppError  
**Обоснование**: Фича остаётся изолированной и не "проталкивает" свои типы ошибок через весь проект.

### (7) UI-state (UiState/UiEvent/UiEffect)

**Q7.1: Базовые типы для State/Event/Effect**  
**Решение**: В `core:ui` определяется общий `UiEffect` (Navigate/Message), остальное (State/Event) — в фичах  
**Обоснование**: Эффекты полезно унифицировать (навигация/тосты), но State/Event сильно завязаны на конкретный экран.

**Q7.2: Базовый ViewModel**  
**Решение**: Частично — утилиты/extension функции и конвенции, без обязательного базового класса  
**Обоснование**: На Step 0 базовый generic-ViewModel часто переусложняет. Лучше закрепить паттерн (State/Event/Effect) и дать лёгкие помощники, не вводя "мини-фреймворк".

**Q7.3: Обработка UiEffect**  
**Решение**: Подписка на SharedFlow через LaunchedEffect в Compose  
**Обоснование**: Стандартный реактивный путь, корректно для одноразовых событий.

### (8) Тестовая стратегия MVP

**Q8.1: Минимальный набор тестов Step 0**  
**Решение**: Unit тесты для базовых типов + интеграционные проверки модульной структуры/сборки  
**Обоснование**: Step 0 — про границы и контракт: важно, чтобы проект собирался и зависимости не нарушали правила.

**Q8.2: Тестирование ViewModel для stub экранов**  
**Решение**: Да, хотя бы один пример теста ViewModel  
**Обоснование**: Задаёт эталонный паттерн тестирования на будущее.

**Q8.3: Тестовые утилиты**  
**Решение**: Да: FakeClock, TestDispatchers, фабрики результатов/ошибок  
**Обоснование**: Ускоряет дальнейшие тесты и делает их единообразными.

