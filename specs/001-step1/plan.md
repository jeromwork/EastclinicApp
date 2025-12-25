# План реализации — Step 1 (Project foundation / App skeleton)

## Принципы
- Минимум инфраструктуры, максимум простоты. Сначала ищем короткое решение, затем добавляем код.
- Соблюдаем конституцию: слои, feature isolation, версии только в catalog, без лишних плагинов.
- Каждая итерация держит билд зелёным.

## Этапы
1) **Bootstrap Gradle и каталог версий**
   - `gradle/libs.versions.toml`: Kotlin, AGP, Compose BOM, Navigation, Hilt, Coroutines, тестовые либы (JUnit5, MockK, Turbine, kotlinx-coroutines-test, Robolectric, compose-ui-test-junit4).
   - `settings.gradle.kts`, root `build.gradle.kts`, `gradle.properties`, wrapper (Gradle 8.5+); единый source для compile/targetSdk и minSdk=24.
   - Проверка: `./gradlew tasks`.

2) **Модули и базовые плагины**
   - Подключить/убедиться в наличии: `app`, `core:common`, `core:async`, `core:ui`, `feature:home:domain`, `feature:home:data`, `feature:home:presentation`.
   - Подключить Compose и Hilt в `app`.
   - Проверка: `./gradlew :app:assembleDebug`.

3) **Core foundation**
   - `core:common`: Result, AppError (минимально, без сети).
   - `core:async`: DispatcherProvider, Clock; тестовые реализации в test-источниках.
   - `core:ui`: тема/typography/spacing, базовый Scaffold, UiEffect, AppDestination/эквивалент.
   - Проверка: сборка core-модулей.

4) **feature:home (3 модуля)**
   - domain: HomeRepository (интерфейс), опциональный GetGreetingUseCase.
   - data: stub HomeRepositoryImpl, Hilt @Binds HomeRepositoryImpl -> HomeRepository.
   - presentation: HomeViewModel, UiState/UiEvent/UiEffect, HomeScreen (stub), маршруты/подграф.
   - Проверка: `./gradlew :feature:home:presentation:build :feature:home:domain:build :feature:home:data:build`.

5) **Навигация**
   - В `feature:home:presentation`: routes + `homeGraph()` (Splash, Home, Settings).
   - В `app`: RootNavGraph композирует `homeGraph()`.
   - Проверка: ручной прогон или простой Compose UI тест.

6) **Тесты (smoke)**
   - Unit: тесты на Result/AppError; тест на HomeViewModel или use-case (JUnit5 + MockK/Turbine + kotlinx-coroutines-test).
   - Compose UI smoke (опционально): отображение ключевого элемента Home (compose-ui-test-junit4). Robolectric — только если нужно для headless.
   - Проверка: `./gradlew test` (UI-тест — если включён).

7) **CI**
   - GitHub Actions workflow: checkout, JDK 17, cache Gradle, `./gradlew build test`.
   - UI-тест в CI опционален; при нестабильности — задокументировать исключение.

8) **Документация**
   - README: структура модулей, зависимости (allowed/forbidden), как добавить фичу (3 модуля, DI биндинг в data, подграф в presentation), когда дробить на подмодули.

9) **Валидация DoD**
   - Локальный билд зелёный, GH Actions зелёный, навигация работает, тесты проходят, правила зависимостей соблюдены.

