# Tasks — Step 1 (Project foundation / App skeleton)

Формат: `[ID] [P?] Description — Done when <критерий>`
- [P] = можно параллельно (без пересечений по файлам)

## Setup / Catalog
- T1 [P] Заполнить `gradle/libs.versions.toml` (Kotlin, AGP, Compose BOM, Navigation, Hilt, Coroutines, JUnit5, MockK, Turbine, kotlinx-coroutines-test, Robolectric, compose-ui-test-junit4).  
  Done when: нет хардкода версий в *.gradle.kts.
- T2 Настроить `settings.gradle.kts`, root `build.gradle.kts`, `gradle.properties`, wrapper 8.5+, единый source для minSdk=24 и target/compileSdk.  
  Done when: `./gradlew tasks` зелёный.

## Modules
- T3 Убедиться/подключить модули: `app`, `core:common`, `core:async`, `core:ui`, `feature:home:domain`, `feature:home:data`, `feature:home:presentation`.  
  Done when: модули в settings, синхронизация без ошибок.
- T4 Подключить Compose и Hilt в `app` (plugins, deps, kapt/ksp).  
  Done when: `./gradlew :app:assembleDebug` зелёный.

## Core foundation
- T5 [P] `core:common`: Result, AppError (минимально, без сети/DTO).  
  Done when: `:core:common:build` зелёный.
- T6 [P] `core:async`: DispatcherProvider, Clock; тестовые реализации в test-источниках.  
  Done when: `:core:async:build` зелёный.
- T7 [P] `core:ui`: тема, typography/spacing, базовый scaffold, UiEffect, AppDestination/эквивалент.  
  Done when: `:core:ui:build` зелёный.

## Feature: home (3 модуля)
- T8 Domain (`feature:home:domain`): HomeRepository (интерфейс), опционально GetGreetingUseCase.  
  Done when: модуль собирается, без Android/Retrofit/Room.
- T9 Data (`feature:home:data`): stub HomeRepositoryImpl + Hilt @Binds HomeRepositoryImpl -> HomeRepository.  
  Done when: модуль собирается, биндинг компилируется.
- T10 Presentation (`feature:home:presentation`): HomeViewModel, UiState/UiEvent/UiEffect, HomeScreen (stub).  
  Done when: `:feature:home:presentation:build` зелёный.

## Navigation
- T11 Routes + `homeGraph()` (Splash, Home, Settings) в `feature:home:presentation`.  
  Done when: подграф компилируется.
- T12 RootNavGraph в `app`, композиция `homeGraph()`.  
  Done when: ручной запуск или UI-тест показывает переходы.

## Tests
- T13 Unit: тесты на Result/AppError; тест на HomeViewModel или use-case (JUnit5 + MockK/Turbine + kotlinx-coroutines-test).  
  Done when: `./gradlew test` зелёный.
- T14 [Опц.] Compose UI smoke: проверка ключевого элемента Home (compose-ui-test-junit4; Robolectric — опционально для headless).  
  Done when: тест проходит локально (и в CI, если включён).

## CI
- T15 GitHub Actions workflow: checkout, JDK 17, cache Gradle, `./gradlew build test`.  
  Done when: workflow зелёный на PR. Если UI-тест нестабилен — документировать исключение.

## Docs
- T16 README: структура, правила зависимостей (allowed/forbidden), как добавить фичу (3 модуля, DI биндинг в data, подграф в presentation), когда делить на подмодули.  
  Done when: по README можно повторить шаги без вопросов.

