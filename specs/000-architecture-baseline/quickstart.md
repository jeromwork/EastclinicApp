# Quickstart Guide: Step 0 — Contract skeleton

**Feature**: Step 0 — Contract skeleton (Architecture baseline)  
**Date**: 2025-01-27

## Prerequisites

- Android Studio Hedgehog (2023.1.1) или новее
- JDK 17 или новее
- Android SDK (API 24+)
- Git

## Setup

### 1. Clone Repository

```bash
git clone https://github.com/jeromwork/EastclinicApp.git
cd EastclinicApp
git checkout 000-architecture-baseline
```

### 2. Open in Android Studio

1. Open Android Studio
2. File → Open → выберите директорию `EastclinicApp`
3. Дождитесь синхронизации Gradle

### 3. Sync Gradle

Android Studio автоматически синхронизирует проект. Если нет:
- File → Sync Project with Gradle Files
- Или нажмите кнопку "Sync Now" в уведомлении

### 4. Build Project

```bash
./gradlew build
```

Ожидаемый результат: сборка завершается успешно без ошибок.

### 5. Run Application

1. Подключите Android устройство или запустите эмулятор
2. В Android Studio: Run → Run 'app'
3. Или из командной строки: `./gradlew :app:installDebug`

Ожидаемый результат: приложение запускается и отображает root screen.

## Project Structure

```
EastclinicApp/
├── app/                    # Application module
├── core/                   # Core modules
│   ├── common/            # Базовые типы (Result, AppError)
│   ├── async/             # Async утилиты (DispatcherProvider, Clock)
│   ├── ui/                # UI компоненты (Theme, UiEffect)
│   ├── network/           # Network инфраструктура
│   ├── auth-contract/     # Auth интерфейсы
│   └── push-contract/     # Push интерфейсы
└── feature/               # Feature modules
    ├── auth/
    ├── home/
    ├── clinics/
    ├── doctors/
    ├── appointments/
    └── chat/
```

Каждый feature состоит из 3 модулей:
- `feature:<x>:presentation` — UI слой
- `feature:<x>:domain` — Domain слой
- `feature:<x>:data` — Data слой

## Navigation

Приложение использует Compose Navigation. Навигация между stub экранами:

1. **Login** → Home
2. **Home** → Clinics
3. **Clinics** → Doctors
4. **Doctors** → Appointments
5. **Appointments** → Chat

Все экраны — stub (заглушки) для демонстрации навигации.

## Adding a New Feature Module

### 1. Create Module Structure

Создайте 3 модуля в `feature/<feature-name>/`:
- `presentation/`
- `domain/`
- `data/`

### 2. Register Modules

Добавьте в `settings.gradle.kts`:

```kotlin
include(":feature:<feature-name>:presentation")
include(":feature:<feature-name>:domain")
include(":feature:<feature-name>:data")
```

### 3. Configure Dependencies

В `build.gradle.kts` каждого модуля:

**presentation**:
```kotlin
dependencies {
    implementation(project(":feature:<feature-name>:domain"))
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
}
```

**domain**:
```kotlin
dependencies {
    implementation(project(":core:common"))
}
```

**data**:
```kotlin
dependencies {
    implementation(project(":feature:<feature-name>:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:common"))
}
```

### 4. Create Package Structure

```
feature/<feature-name>/
├── presentation/
│   └── src/main/java/com/eastclinic/<feature-name>/presentation/
│       ├── <Feature>Screen.kt
│       ├── <Feature>ViewModel.kt
│       ├── <Feature>UiState.kt
│       ├── <Feature>UiEvent.kt
│       ├── <Feature>UiEffect.kt
│       └── navigation/
│           ├── <Feature>Routes.kt
│           └── <feature>Graph.kt
├── domain/
│   └── src/main/java/com/eastclinic/<feature-name>/domain/
│       ├── model/
│       ├── repository/
│       └── usecase/
└── data/
    └── src/main/java/com/eastclinic/<feature-name>/data/
        ├── repository/
        └── di/
```

### 5. Add Navigation

1. Создайте routes в `feature:<feature-name>:presentation`
2. Создайте функцию `NavGraphBuilder.<feature>Graph()` 
3. Добавьте вызов в `app/navigation/RootNavGraph.kt`

### 6. Add DI Module

Создайте Hilt модуль в `feature:<feature-name>:data/di/`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
interface <Feature>DataModule {
    @Binds
    fun bindRepository(
        impl: <Feature>RepositoryImpl
    ): <Feature>Repository
}
```

## Testing

### Run All Tests

```bash
./gradlew test
```

### Run Tests for Specific Module

```bash
./gradlew :core:common:test
```

### Run Instrumented Tests

```bash
./gradlew connectedAndroidTest
```

## Common Commands

```bash
# Build
./gradlew build

# Clean
./gradlew clean

# Assemble Debug APK
./gradlew :app:assembleDebug

# Install on device
./gradlew :app:installDebug

# Run tests
./gradlew test

# Check dependencies
./gradlew :app:dependencies
```

## Troubleshooting

### Build Fails

1. Проверьте версию JDK: `java -version` (должна быть 17+)
2. Очистите кеш: `./gradlew clean`
3. Invalidate caches в Android Studio: File → Invalidate Caches

### Navigation Not Working

1. Убедитесь, что routes правильно определены
2. Проверьте, что подграфы подключены в RootNavGraph
3. Проверьте обработку UiEffect.Navigate в app модуле

### DI Not Working

1. Убедитесь, что @HiltAndroidApp в Application классе
2. Проверьте, что Hilt модули правильно установлены
3. Проверьте зависимости в build.gradle.kts

## Next Steps

После завершения Step 0:

1. Реализовать реальную логику в feature модулях
2. Интегрировать с backend API
3. Добавить локальное хранилище (core:database) при необходимости
4. Реализовать push уведомления
5. Добавить реальную авторизацию

## Resources

- [Specification](./spec.md)
- [Implementation Plan](./plan.md)
- [Architectural Decisions](./CLARIFICATIONS.md)
- [Constitution](../../.specify/memory/constitution.md)

