# Tasks: Step 0 — Contract skeleton (Architecture baseline)

**Input**: Design documents from `/specs/000-architecture-baseline/`  
**Prerequisites**: plan.md, spec.md, data-model.md, contracts/

**Tests**: Тесты включены согласно спецификации (SC-014, SC-015, SC-016, SC-017)

**Organization**: Задачи организованы по user stories для независимой реализации и тестирования каждой истории.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Может выполняться параллельно (разные файлы, нет зависимостей)
- **[Story]**: К какой user story относится задача (US1, US2, US3, US4, US5, US6)
- В описании указаны точные пути к файлам

## Path Conventions

- Android multi-module проект
- Модули: `app/`, `core/*/`, `feature/*/`
- Исходный код: `src/main/java/com/eastclinic/...`
- Тесты: `src/test/java/...` или `src/androidTest/java/...`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Инициализация проекта и базовая структура Gradle

**Done when**: `./gradlew tasks` выполняется без ошибок

- [ ] T001 Create root `build.gradle.kts` with Android Gradle Plugin 8.1+ and Kotlin 1.9+ in `build.gradle.kts`
- [ ] T002 Create `settings.gradle.kts` with module registration structure in `settings.gradle.kts`
- [ ] T003 [P] Create `gradle/libs.versions.toml` with version catalog including all versions (Kotlin, AGP, Compose BOM, Hilt, Navigation, Retrofit, OkHttp, Coroutines, JUnit, MockK, Turbine, Compose Testing) in `gradle/libs.versions.toml`
- [ ] T004 [P] Create `gradle.properties` with Android build properties (org.gradle.jvmargs, android.useAndroidX, etc.) in `gradle.properties`
- [ ] T005 [P] Create `gradle/wrapper/gradle-wrapper.properties` with Gradle 8.0+ in `gradle/wrapper/gradle-wrapper.properties`
- [ ] T005a [P] Verify all dependencies use version catalog: check that no hardcoded versions in build.gradle.kts files, all use `libs.versions.*`

**Commands to verify**: `./gradlew tasks`

**Scope guard**: Не создавать модули на этом этапе, только структуру Gradle

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core инфраструктура, которая ДОЛЖНА быть завершена перед ЛЮБОЙ user story

**⚠️ CRITICAL**: Никакая работа над user stories не может начаться, пока эта фаза не завершена

**Done when**: Все core модули компилируются, тесты проходят, Hilt настроен

### Core Modules (порядок важен для зависимостей)

- [ ] T006 Create `core/common/build.gradle.kts` with Kotlin library plugin and dependencies in `core/common/build.gradle.kts`
- [ ] T007 [US3] Create `Result.kt` sealed class in `core/common/src/main/java/com/eastclinic/core/common/Result.kt`
- [ ] T008 [US3] Create `AppError.kt` sealed class with NetworkError, ValidationError, UnknownError in `core/common/src/main/java/com/eastclinic/core/common/AppError.kt`
- [ ] T009 Register `core:common` module in `settings.gradle.kts`
- [ ] T010 Verify `core:common` compiles: `./gradlew :core:common:build`

- [ ] T011 Create `core/async/build.gradle.kts` with dependency on `core:common` in `core/async/build.gradle.kts`
- [ ] T012 [US3] Create `DispatcherProvider.kt` interface in `core/async/src/main/java/com/eastclinic/core/async/DispatcherProvider.kt`
- [ ] T013 [US3] Create `Clock.kt` interface in `core/async/src/main/java/com/eastclinic/core/async/Clock.kt`
- [ ] T014 Register `core:async` module in `settings.gradle.kts`
- [ ] T015 Verify `core:async` compiles: `./gradlew :core:async:build`

- [ ] T016 Create `core/ui/build.gradle.kts` with Compose dependencies and `core:common` in `core/ui/build.gradle.kts`
- [ ] T017 [US4] Create `UiEffect.kt` sealed class with Navigate and ShowMessage in `core/ui/src/main/java/com/eastclinic/core/ui/UiEffect.kt`
- [ ] T018 [US5] Create `AppDestination.kt` interface/type for navigation routes in `core/ui/src/main/java/com/eastclinic/core/ui/AppDestination.kt`
- [ ] T019 [US4] Create basic theme structure in `core/ui/src/main/java/com/eastclinic/core/ui/theme/Theme.kt`
- [ ] T020 Register `core:ui` module in `settings.gradle.kts`
- [ ] T021 Verify `core:ui` compiles: `./gradlew :core:ui:build`

- [ ] T022 Create `core/auth-contract/build.gradle.kts` with dependency on `core:common` in `core/auth-contract/build.gradle.kts`
- [ ] T023 Create `AuthTokenProvider.kt` interface in `core/auth-contract/src/main/java/com/eastclinic/core/auth/AuthTokenProvider.kt`
- [ ] T024 Create `SessionStore.kt` interface in `core/auth-contract/src/main/java/com/eastclinic/core/auth/SessionStore.kt`
- [ ] T025 Register `core:auth-contract` module in `settings.gradle.kts`
- [ ] T026 Verify `core:auth-contract` compiles: `./gradlew :core:auth-contract:build`

- [ ] T027 Create `core/push-contract/build.gradle.kts` with dependency on `core:common` in `core/push-contract/build.gradle.kts`
- [ ] T028 Create `PushTokenProvider.kt` interface in `core/push-contract/src/main/java/com/eastclinic/core/push/PushTokenProvider.kt`
- [ ] T029 Create `NotificationHandler.kt` interface in `core/push-contract/src/main/java/com/eastclinic/core/push/NotificationHandler.kt`
- [ ] T030 Register `core:push-contract` module in `settings.gradle.kts`
- [ ] T031 Verify `core:push-contract` compiles: `./gradlew :core:push-contract:build`

- [ ] T032 Create `core/network/build.gradle.kts` with Retrofit/OkHttp dependencies, `core:common`, `core:auth-contract` in `core/network/build.gradle.kts`
- [ ] T033 [US3] Create `NetworkResult.kt` sealed class in `core/network/src/main/java/com/eastclinic/core/network/NetworkResult.kt`
- [ ] T034 [US3] Create `NetworkError.kt` data class in `core/network/src/main/java/com/eastclinic/core/network/NetworkError.kt`
- [ ] T035 [US3] Create `NetworkErrorMapper.kt` extension function `NetworkError.toAppError()` in `core/network/src/main/java/com/eastclinic/core/network/NetworkErrorMapper.kt`
- [ ] T036 [US3] Create `safeCall.kt` suspend function with error mapping in `core/network/src/main/java/com/eastclinic/core/network/safeCall.kt`
- [ ] T037 Register `core:network` module in `settings.gradle.kts`
- [ ] T038 Verify `core:network` compiles: `./gradlew :core:network:build`

**Commands to verify**: `./gradlew :core:common:build :core:async:build :core:ui:build :core:auth-contract:build :core:push-contract:build :core:network:build`

**Scope guard**: Не создавать реализации, только интерфейсы и базовые типы. Не создавать feature модули.

### Test Infrastructure

- [ ] T039 [US6] Create `FakeClock.kt` test implementation in `core/async/src/test/java/com/eastclinic/core/async/FakeClock.kt`
- [ ] T040 [US6] Create `TestDispatchers.kt` test implementation in `core/async/src/test/java/com/eastclinic/core/async/TestDispatchers.kt`
- [ ] T041 [US6] Create `ResultFactory.kt` test utilities in `core/common/src/test/java/com/eastclinic/core/common/ResultFactory.kt`
- [ ] T042 [US6] Create `AppErrorFactory.kt` test utilities in `core/common/src/test/java/com/eastclinic/core/common/AppErrorFactory.kt`
- [ ] T043 [US6] Add test dependencies (JUnit 5, MockK, Turbine) to `core/common/build.gradle.kts` and `core/async/build.gradle.kts`

**Commands to verify**: `./gradlew :core:common:test :core:async:test`

**Scope guard**: Только тестовые утилиты, не тесты для базовых типов (это в US6)

### Hilt Setup

- [ ] T044 Create `app/build.gradle.kts` with Android application plugin, Hilt, Compose dependencies in `app/build.gradle.kts`
- [ ] T045 Create `EastclinicApplication.kt` with `@HiltAndroidApp` in `app/src/main/java/com/eastclinic/app/EastclinicApplication.kt`
- [ ] T046 Create `AndroidManifest.xml` with Application class reference in `app/src/main/AndroidManifest.xml`
- [ ] T047 Create `AppModule.kt` Hilt module in `app/src/main/java/com/eastclinic/app/di/AppModule.kt`
- [ ] T048 Register `app` module in `settings.gradle.kts`
- [ ] T049 Verify app compiles: `./gradlew :app:assembleDebug`

### Example Hilt Module for Feature

- [ ] T050 [US2] Create example `AuthDataModule.kt` Hilt module with `@Binds` for AuthRepository in `feature/auth/data/src/main/java/com/eastclinic/auth/data/di/AuthDataModule.kt`
- [ ] T051 [US2] Create stub `AuthRepositoryImpl.kt` implementing AuthRepository interface in `feature/auth/data/src/main/java/com/eastclinic/auth/data/repository/AuthRepositoryImpl.kt`
- [ ] T052 [US2] Verify Hilt module compiles and binds correctly: `./gradlew :feature:auth:data:build`

**Commands to verify**: `./gradlew :app:assembleDebug`

**Scope guard**: Не создавать экраны и навигацию, только базовую структуру app модуля

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Application Launches Successfully (Priority: P1) 🎯 MVP

**Goal**: Приложение собирается и запускается, отображает root screen

**Independent Test**: `./gradlew :app:assembleDebug` успешно, приложение запускается на устройстве/эмуляторе и показывает root screen

**Done when**: Приложение запускается без крашей, отображается простой экран

- [ ] T053 [US1] Create minimal `MainActivity.kt` with Compose in `app/src/main/java/com/eastclinic/app/MainActivity.kt`
- [ ] T054 [US1] Create `RootScreen.kt` stub composable in `app/src/main/java/com/eastclinic/app/RootScreen.kt`
- [ ] T055 [US1] Update `AndroidManifest.xml` with MainActivity launcher in `app/src/main/AndroidManifest.xml`
- [ ] T056 [US1] Verify app builds and launches: `./gradlew :app:installDebug` then launch on device

**Commands to verify**: `./gradlew :app:assembleDebug && ./gradlew :app:installDebug`

**Scope guard**: Только минимальный экран, без навигации и feature модулей

**Checkpoint**: User Story 1 complete - app launches successfully

---

## Phase 4: User Story 2 - Module Structure and Boundaries Established (Priority: P1)

**Goal**: Создана структура всех модулей с правильными зависимостями, соблюдены границы Clean Architecture

**Independent Test**: Все модули компилируются отдельно, зависимости проверены через `./gradlew :app:dependencies`, domain модули не содержат Android/Retrofit/Room типов

**Done when**: Все core и feature модули созданы, компилируются, зависимости корректны

### Feature Modules Structure (auth, home, clinics, doctors, appointments, chat)

Для каждого feature (auth, home, clinics, doctors, appointments, chat):

- [ ] T057 [US2] Create `feature/<feature>/presentation/build.gradle.kts` with dependencies on `:feature:<feature>:domain`, `:core:ui`, `:core:common` in `feature/<feature>/presentation/build.gradle.kts`
- [ ] T058 [US2] Create `feature/<feature>/domain/build.gradle.kts` with dependency only on `:core:common` in `feature/<feature>/domain/build.gradle.kts`
- [ ] T059 [US2] Create `feature/<feature>/data/build.gradle.kts` with dependencies on `:feature:<feature>:domain`, `:core:network`, `:core:common`, Hilt in `feature/<feature>/data/build.gradle.kts`
- [ ] T060 [US2] Register all three modules in `settings.gradle.kts`
- [ ] T061 [US2] Verify each module compiles: `./gradlew :feature:<feature>:presentation:build :feature:<feature>:domain:build :feature:<feature>:data:build`

**Commands to verify**: `./gradlew build` (все модули)

**Scope guard**: Только структура модулей и build.gradle.kts, не создавать код внутри модулей

**Checkpoint**: User Story 2 complete - module structure established

---

## Phase 5: User Story 3 - Base Types and Abstractions Available (Priority: P1)

**Goal**: Базовые типы (AppError, Result<T>, DispatcherProvider, Clock) доступны и используются в feature модулях

**Independent Test**: Feature модуль может импортировать и использовать базовые типы, unit тесты для базовых типов проходят

**Done when**: Базовые типы реализованы, есть unit тесты, feature модуль может их использовать

### Unit Tests for Base Types

- [ ] T062 [US6] [US3] Create `ResultTest.kt` with tests for Success and Error variants in `core/common/src/test/java/com/eastclinic/core/common/ResultTest.kt`
- [ ] T063 [US6] [US3] Create `AppErrorTest.kt` with tests for all sealed class variants in `core/common/src/test/java/com/eastclinic/core/common/AppErrorTest.kt`
- [ ] T064 [US6] [US3] Create `NetworkErrorMapperTest.kt` with tests for NetworkError.toAppError() mapping in `core/network/src/test/java/com/eastclinic/core/network/NetworkErrorMapperTest.kt`
- [ ] T065 [US6] [US3] Verify tests pass: `./gradlew :core:common:test :core:network:test`

### Example Usage in Feature Module

- [ ] T066 [US3] Create stub `User.kt` domain model using Result<T> in `feature/auth/domain/src/main/java/com/eastclinic/auth/domain/model/User.kt`
- [ ] T067 [US3] Create stub `AuthRepository.kt` interface returning Result<User> in `feature/auth/domain/src/main/java/com/eastclinic/auth/domain/repository/AuthRepository.kt`
- [ ] T068 [US3] Verify feature:auth:domain compiles with base types: `./gradlew :feature:auth:domain:build`

**Commands to verify**: `./gradlew :core:common:test :feature:auth:domain:build`

**Scope guard**: Только демонстрация использования базовых типов, не полная реализация auth

**Checkpoint**: User Story 3 complete - base types available and tested

---

## Phase 6: User Story 4 - UI Architecture Pattern Established (Priority: P1)

**Goal**: Установлен паттерн UiState + UiEvent + UiEffect, хотя бы один stub экран демонстрирует паттерн

**Independent Test**: Stub экран использует UiState/Event/Effect, ViewModel экспонирует StateFlow, обрабатывает события, эмитит эффекты через SharedFlow

**Done when**: Один stub экран (например, LoginScreen) реализует полный паттерн, UiEffect обрабатывается через LaunchedEffect

### Stub Screen Implementation (LoginScreen as example)

- [ ] T069 [US4] Create `LoginUiState.kt` data class in `feature/auth/presentation/src/main/java/com/eastclinic/auth/presentation/login/LoginUiState.kt`
- [ ] T070 [US4] Create `LoginUiEvent.kt` sealed class in `feature/auth/presentation/src/main/java/com/eastclinic/auth/presentation/login/LoginUiEvent.kt`
- [ ] T071 [US4] Create `LoginUiEffect.kt` sealed class extending UiEffect in `feature/auth/presentation/src/main/java/com/eastclinic/auth/presentation/login/LoginUiEffect.kt`
- [ ] T072 [US4] Create `LoginViewModel.kt` with StateFlow<UiState>, event handling, SharedFlow<UiEffect> in `feature/auth/presentation/src/main/java/com/eastclinic/auth/presentation/login/LoginViewModel.kt`
- [ ] T073 [US4] Create `LoginScreen.kt` composable with LaunchedEffect for UiEffect handling in `feature/auth/presentation/src/main/java/com/eastclinic/auth/presentation/login/LoginScreen.kt`
- [ ] T074 [US4] Verify feature:auth:presentation compiles: `./gradlew :feature:auth:presentation:build`

**Commands to verify**: `./gradlew :feature:auth:presentation:build`

**Scope guard**: Только паттерн, не реальная логика авторизации

**Checkpoint**: User Story 4 complete - UI pattern established

---

## Phase 7: User Story 5 - End-to-End Navigation Working (Priority: P2)

**Goal**: Работает навигация между stub экранами через Compose Navigation, подграфы в feature модулях, композиция в app

**Independent Test**: Приложение запускается, можно навигировать между всеми stub экранами (Login → Home → Clinics → Doctors → Appointments → Chat), back navigation работает

**Done when**: Все stub экраны созданы, навигация работает end-to-end

### Navigation Routes in Feature Modules

- [ ] T075 [US5] Create `AuthRoutes.kt` with route constants in `feature/auth/presentation/src/main/java/com/eastclinic/auth/presentation/navigation/AuthRoutes.kt`
- [ ] T076 [US5] Create `authGraph()` function in `feature/auth/presentation/src/main/java/com/eastclinic/auth/presentation/navigation/authGraph.kt`
- [ ] T077 [US5] Repeat T075-T076 for home, clinics, doctors, appointments, chat features

### Stub Screens for All Features

- [ ] T078 [US5] Create `HomeScreen.kt` stub in `feature/home/presentation/src/main/java/com/eastclinic/home/presentation/HomeScreen.kt`
- [ ] T079 [US5] Create `ClinicsScreen.kt` stub in `feature/clinics/presentation/src/main/java/com/eastclinic/clinics/presentation/ClinicsScreen.kt`
- [ ] T080 [US5] Create `DoctorsScreen.kt` stub in `feature/doctors/presentation/src/main/java/com/eastclinic/doctors/presentation/DoctorsScreen.kt`
- [ ] T081 [US5] Create `AppointmentsScreen.kt` stub in `feature/appointments/presentation/src/main/java/com/eastclinic/appointments/presentation/AppointmentsScreen.kt`
- [ ] T082 [US5] Create `ChatScreen.kt` stub in `feature/chat/presentation/src/main/java/com/eastclinic/chat/presentation/ChatScreen.kt`

### Root Navigation Graph

- [ ] T083 [US5] Create `RootNavGraph.kt` composing all feature subgraphs in `app/src/main/java/com/eastclinic/app/navigation/RootNavGraph.kt`
- [ ] T084 [US5] Update `MainActivity.kt` to use RootNavGraph in `app/src/main/java/com/eastclinic/app/MainActivity.kt`
- [ ] T085 [US5] Implement `UiEffect.Navigate` handling in app module in `app/src/main/java/com/eastclinic/app/navigation/NavigationHandler.kt`

### Navigation Flow Implementation

- [ ] T086 [US5] Connect LoginScreen → HomeScreen navigation via UiEffect.Navigate in `feature/auth/presentation/src/main/java/com/eastclinic/auth/presentation/login/LoginViewModel.kt`
- [ ] T087 [US5] Connect HomeScreen → ClinicsScreen navigation
- [ ] T088 [US5] Connect ClinicsScreen → DoctorsScreen navigation
- [ ] T089 [US5] Connect DoctorsScreen → AppointmentsScreen navigation
- [ ] T090 [US5] Connect AppointmentsScreen → ChatScreen navigation
- [ ] T091 [US5] Verify navigation works: launch app and navigate through all screens

### UI Tests for Navigation

- [ ] T092 [US6] [US5] Add Compose Testing dependency to `app/build.gradle.kts`
- [ ] T093 [US6] [US5] Create `NavigationTest.kt` UI test verifying navigation between stub screens in `app/src/androidTest/java/com/eastclinic/app/navigation/NavigationTest.kt`
- [ ] T094 [US6] [US5] Create `ScreenDisplayTest.kt` UI test verifying stub screens display correctly in `app/src/androidTest/java/com/eastclinic/app/ui/ScreenDisplayTest.kt`
- [ ] T095 [US6] [US5] Verify UI tests pass: `./gradlew :app:connectedAndroidTest` (requires device/emulator)

**Commands to verify**: `./gradlew :app:assembleDebug && ./gradlew :app:installDebug`, then manual navigation test

**Scope guard**: Только навигация между stub экранами, не реальная логика фич

**Checkpoint**: User Story 5 complete - navigation working end-to-end

---

## Phase 8: User Story 6 - Testing Infrastructure Established (Priority: P2)

**Goal**: Тестовая инфраструктура создана, есть примеры тестов для базовых типов и ViewModel

**Independent Test**: Unit тесты для Result/AppError проходят, пример теста ViewModel демонстрирует паттерн, тестовые утилиты используются

**Done when**: Все тесты проходят, есть пример теста ViewModel, тестовые утилиты доступны

### ViewModel Test Example

- [ ] T096 [US6] [US4] Create `LoginViewModelTest.kt` demonstrating UiState/UiEvent/UiEffect testing in `feature/auth/presentation/src/test/java/com/eastclinic/auth/presentation/login/LoginViewModelTest.kt`
- [ ] T097 [US6] [US4] Verify ViewModel test passes: `./gradlew :feature:auth:presentation:test`

### Integration Tests for Module Structure

- [ ] T098 [US6] [US2] Create `ModuleDependencyTest.kt` verifying no Android types in domain modules in `app/src/test/java/com/eastclinic/app/ModuleDependencyTest.kt`
- [ ] T099 [US6] [US2] Create `DependencyGraphTest.kt` verifying acyclic core dependencies in `app/src/test/java/com/eastclinic/app/DependencyGraphTest.kt`
- [ ] T100 [US6] Verify integration tests pass: `./gradlew :app:test`

### Dependency Graph Analysis

- [ ] T101 [US2] Create script or task to analyze dependency graph: `./gradlew :app:dependencies > dependencies.txt`
- [ ] T102 [US2] Verify no circular dependencies exist in dependency graph output
- [ ] T103 [US2] Verify core:auth-contract does NOT depend on core:network in dependency graph
- [ ] T104 [US2] Verify all domain modules have no Android/Retrofit/Room dependencies (manual code review or script)

**Commands to verify**: `./gradlew test`

**Scope guard**: Только примеры тестов, не полное покрытие всех модулей

**Checkpoint**: User Story 6 complete - testing infrastructure established

---

## Phase 9: Polish & Cross-Cutting Concerns

**Purpose**: Финальные проверки и улучшения

**Done when**: Все проверки пройдены, проект готов к разработке фич

- [ ] T105 [P] Run full build: `./gradlew clean build`
- [ ] T106 [P] Run all unit tests: `./gradlew test`
- [ ] T107 [P] Run all UI tests: `./gradlew connectedAndroidTest` (requires device/emulator)
- [ ] T108 [P] Verify dependency graph: run T101-T104 tasks
- [ ] T109 [P] Create minimal CI workflow file `.github/workflows/build.yml` with build, test, and dependency check steps
- [ ] T110 [P] Update `quickstart.md` with actual setup instructions if needed
- [ ] T111 [P] Verify app launches and navigation works on device/emulator

**Commands to verify**: `./gradlew clean build test`

**Scope guard**: Только финальные проверки, не добавлять новую функциональность

**Checkpoint**: Step 0 complete - ready for feature development

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Нет зависимостей - можно начать сразу
- **Foundational (Phase 2)**: Зависит от Setup - БЛОКИРУЕТ все user stories
- **User Stories (Phase 3-8)**: Все зависят от Foundational
  - US1 может начаться сразу после Foundational
  - US2 может начаться параллельно с US1 (разные модули)
  - US3 зависит от US2 (нужны feature модули для демонстрации)
  - US4 зависит от US3 (нужны базовые типы)
  - US5 зависит от US4 (нужен UI паттерн)
  - US6 может начаться параллельно с US5 (тесты независимы)
- **Polish (Phase 9)**: Зависит от завершения всех user stories

### User Story Dependencies

- **User Story 1 (P1)**: Может начаться после Foundational - нет зависимостей от других stories
- **User Story 2 (P1)**: Может начаться после Foundational - независима от US1
- **User Story 3 (P1)**: Зависит от US2 (нужны feature модули) - может начаться после US2
- **User Story 4 (P1)**: Зависит от US3 (нужны базовые типы) - может начаться после US3
- **User Story 5 (P2)**: Зависит от US4 (нужен UI паттерн) - может начаться после US4
- **User Story 6 (P2)**: Может начаться параллельно с US5 - независима

### Within Each User Story

- Core модули создаются в порядке зависимостей (common → async/ui → network)
- Feature модули могут создаваться параллельно (разные фичи)
- Тесты пишутся после реализации (или параллельно для разных модулей)
- Навигация настраивается после создания всех экранов

### Parallel Opportunities

- **Phase 1**: T003, T004, T005 могут выполняться параллельно
- **Phase 2 Core Modules**: После T010, модули async, ui, auth-contract, push-contract могут создаваться параллельно (T011-T031)
- **Phase 2 Test Infrastructure**: T038-T041 могут выполняться параллельно
- **Phase 4**: Все feature модули (auth, home, clinics, doctors, appointments, chat) могут создаваться параллельно
- **Phase 7**: Stub экраны (T073-T077) могут создаваться параллельно
- **Phase 8**: Тесты могут писаться параллельно для разных модулей
- **Phase 9**: Все задачи помечены [P] - могут выполняться параллельно

---

## Parallel Example: Phase 2 Core Modules

```bash
# После создания core:common, можно параллельно создавать:
Task T011: Create core/async/build.gradle.kts
Task T016: Create core/ui/build.gradle.kts  
Task T022: Create core/auth-contract/build.gradle.kts
Task T027: Create core/push-contract/build.gradle.kts

# После этого параллельно создавать интерфейсы:
Task T012: Create DispatcherProvider.kt
Task T017: Create UiEffect.kt
Task T023: Create AuthTokenProvider.kt
Task T028: Create PushTokenProvider.kt
```

---

## Parallel Example: Phase 4 Feature Modules

```bash
# Все feature модули могут создаваться параллельно:
Task T053: Create feature/auth/presentation/build.gradle.kts
Task T053: Create feature/home/presentation/build.gradle.kts
Task T053: Create feature/clinics/presentation/build.gradle.kts
Task T053: Create feature/doctors/presentation/build.gradle.kts
Task T053: Create feature/appointments/presentation/build.gradle.kts
Task T053: Create feature/chat/presentation/build.gradle.kts

# И аналогично для domain и data модулей
```

---

## Implementation Strategy

### MVP First (User Stories 1-4 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (App Launches)
4. Complete Phase 4: User Story 2 (Module Structure)
5. Complete Phase 5: User Story 3 (Base Types)
6. Complete Phase 6: User Story 4 (UI Pattern)
7. **STOP and VALIDATE**: Проверить, что всё работает
8. Это минимальный MVP для архитектурного каркаса

### Incremental Delivery

1. Setup + Foundational → Foundation ready
2. US1 → App launches → Validate
3. US2 → Module structure → Validate
4. US3 → Base types → Validate
5. US4 → UI pattern → Validate
6. US5 → Navigation → Validate (опционально для MVP)
7. US6 → Testing → Validate (опционально для MVP)

### Solo Developer Strategy

Для соло-разработчика рекомендуется последовательное выполнение:

1. Phase 1: Setup (1-2 часа)
2. Phase 2: Foundational (4-6 часов, можно разбить на дни)
3. Phase 3: US1 (1 час)
4. Phase 4: US2 (2-3 часа)
5. Phase 5: US3 (1-2 часа)
6. Phase 6: US4 (2-3 часа)
7. Phase 7: US5 (4-5 часов, опционально) - включает UI тесты
8. Phase 8: US6 (3-4 часа, опционально) - включает dependency graph analysis
9. Phase 9: Polish (1-2 часа)

**Общее время MVP (US1-US4)**: ~12-18 часов  
**Общее время полного Step 0**: ~20-28 часов (с учетом UI тестов и dependency analysis)

---

## Notes

- [P] задачи = разные файлы, нет зависимостей
- [Story] метка связывает задачу с конкретной user story для трассируемости
- Каждая user story должна быть независимо завершаемой и тестируемой
- Коммитить после каждой задачи или логической группы
- Останавливаться на любом checkpoint для валидации story независимо
- Избегать: расплывчатых задач, конфликтов в одном файле, зависимостей между stories, которые ломают независимость
- Все задачи рассчитаны на 1-3 часа для новичка
- Scope guard явно указывает, что НЕ делать в каждой фазе

---

## Out of Scope for Step 0

Следующее НЕ делается в Step 0, но оставляются точки расширения:

- **Эквайринг банка**: Только интерфейсы/точки расширения в core модулях
- **ЕСИА/OAuth2/OIDC интеграция**: Только контракты в core:auth-contract, без реализации
- **Реальные push через FCM**: Только контракты в core:push-contract, без реализации
- **Реальный чат/медиа**: Только каркас модулей feature:chat, без логики
- **core:database**: Отложен до первой реальной локальной сущности
- **Реальные API запросы**: Только каркас core:network, без реальных endpoints
- **Полная реализация feature логики**: Только skeleton/stub реализации

