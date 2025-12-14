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

### 6. Create Domain Layer

**Domain Model** (`feature/<feature-name>/domain/src/main/java/com/eastclinic/<feature-name>/domain/model/`):

```kotlin
// Example: User.kt
data class User(
    val id: String,
    val name: String
)
```

**Repository Interface** (`feature/<feature-name>/domain/src/main/java/com/eastclinic/<feature-name>/domain/repository/`):

```kotlin
// Example: UserRepository.kt
interface UserRepository {
    suspend fun getUser(id: String): Result<User>
    fun observeUsers(): Flow<Result<List<User>>>
}
```

**Важно**: Domain слой использует только `Result<T>` и `AppError` из `core:common`, никаких Android/Retrofit/Room типов.

### 7. Create Data Layer

**Repository Implementation** (`feature/<feature-name>/data/src/main/java/com/eastclinic/<feature-name>/data/repository/`):

```kotlin
// Example: UserRepositoryImpl.kt
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: UserApi, // Retrofit interface
    private val dispatcherProvider: DispatcherProvider
) : UserRepository {
    
    override suspend fun getUser(id: String): Result<User> {
        return when (val networkResult = safeCall(dispatcherProvider.io) {
            api.getUser(id)
        }) {
            is NetworkResult.Success -> Result.Success(networkResult.data.toDomain())
            is NetworkResult.Error -> Result.Error(networkResult.error.toAppError())
        }
    }
    
    override fun observeUsers(): Flow<Result<List<User>>> = flow {
        val users = fetchUsers()
        emit(Result.Success(users))
    }.catch { exception ->
        val networkError = mapExceptionToNetworkError(exception)
        emit(Result.Error(networkError.toAppError()))
    }.flowOn(dispatcherProvider.io)
}
```

**Важно**: 
- Используйте `safeCall()` для suspend функций
- Используйте `Flow.catch {}` для Flow операций
- Всегда маппите `NetworkError` в `AppError` через `toAppError()`

### 8. Add Hilt DI Module

Создайте Hilt модуль в `feature/<feature-name>/data/src/main/java/com/eastclinic/<feature-name>/data/di/`:

```kotlin
// Example: UserDataModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class UserDataModule {
    
    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
    
    companion object {
        @Provides
        @Singleton
        fun provideUserApi(retrofit: Retrofit): UserApi {
            return retrofit.create(UserApi::class.java)
        }
    }
}
```

**Важно**: 
- Используйте `@Binds` для интерфейсов
- Используйте `@Provides` для конкретных типов (Retrofit, OkHttp и т.д.)
- Модуль должен быть в data слое, где находится реализация

### 9. Create Presentation Layer

**UiState** (`feature/<feature-name>/presentation/src/main/java/com/eastclinic/<feature-name>/presentation/`):

```kotlin
// Example: UserUiState.kt
data class UserUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: AppError? = null
)
```

**UiEvent**:

```kotlin
// Example: UserUiEvent.kt
sealed class UserUiEvent {
    data class LoadUser(val id: String) : UserUiEvent()
    object Refresh : UserUiEvent()
}
```

**UiEffect**:

```kotlin
// Example: UserUiEffect.kt
sealed class UserUiEffect : UiEffect {
    data class NavigateToDetails(val userId: String) : UserUiEffect()
    data class ShowError(val message: String) : UserUiEffect()
}
```

**ViewModel**:

```kotlin
// Example: UserViewModel.kt
@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()
    
    private val _uiEffect = MutableSharedFlow<UserUiEffect>()
    val uiEffect: SharedFlow<UserUiEffect> = _uiEffect.asSharedFlow()
    
    fun handleEvent(event: UserUiEvent) {
        when (event) {
            is UserUiEvent.LoadUser -> loadUser(event.id)
            is UserUiEvent.Refresh -> refresh()
        }
    }
    
    private fun loadUser(id: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = userRepository.getUser(id)) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(isLoading = false, user = result.data) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.error) 
                    }
                    _uiEffect.emit(UserUiEffect.ShowError(result.error.message))
                }
            }
        }
    }
}
```

**Screen**:

```kotlin
// Example: UserScreen.kt
@Composable
fun UserScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val uiEffect = remember { viewModel.uiEffect }
    
    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is UserUiEffect.NavigateToDetails -> 
                    onNavigate(effect.userId)
                is UserUiEffect.ShowError -> 
                    // Show snackbar or toast
            }
        }
    }
    
    // UI implementation
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

### Run UI Tests (Compose Testing)

```bash
./gradlew connectedAndroidTest
```

**Требования**: UI тесты требуют запущенного эмулятора или подключенного устройства.

### Testing Patterns

**Unit Tests для ViewModel**:

```kotlin
@Test
fun `when LoadUser event, then state updates correctly`() = runTest {
    // Given
    val repository = mockk<UserRepository>()
    coEvery { repository.getUser("1") } returns Result.Success(User("1", "Test"))
    
    // When
    val viewModel = UserViewModel(repository, TestDispatchers())
    viewModel.handleEvent(UserUiEvent.LoadUser("1"))
    
    // Then
    val state = viewModel.uiState.value
    assertEquals(false, state.isLoading)
    assertEquals("Test", state.user?.name)
}
```

**UI Tests для Navigation**:

```kotlin
@Test
fun testNavigationBetweenScreens() {
    composeTestRule.setContent {
        RootNavGraph()
    }
    
    composeTestRule.onNodeWithText("Login").performClick()
    composeTestRule.onNodeWithText("Home").assertIsDisplayed()
}
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

## Dependency Graph Analysis

### Check Dependencies

```bash
# Получить полный граф зависимостей
./gradlew :app:dependencies > dependencies.txt

# Проверить зависимости конкретного модуля
./gradlew :feature:auth:domain:dependencies
```

### Verify Architecture Rules

1. **Domain модули не должны зависеть от Android/Retrofit/Room**:
   ```bash
   # Проверить зависимости domain модуля
   ./gradlew :feature:auth:domain:dependencies | grep -i "android\|retrofit\|room"
   # Должно быть пусто
   ```

2. **Core модули не должны иметь циклических зависимостей**:
   ```bash
   # Проверить, что core:auth-contract не зависит от core:network
   ./gradlew :core:auth-contract:dependencies | grep "core:network"
   # Должно быть пусто
   ```

3. **Feature модули не должны зависеть друг от друга**:
   ```bash
   # Проверить зависимости feature модуля
   ./gradlew :feature:auth:presentation:dependencies | grep "feature:"
   # Должны быть только зависимости от собственных модулей (auth:domain, auth:data)
   ```

## Troubleshooting

### Build Fails

1. Проверьте версию JDK: `java -version` (должна быть 17+)
2. Очистите кеш: `./gradlew clean`
3. Invalidate caches в Android Studio: File → Invalidate Caches
4. Проверьте, что все версии в `libs.versions.toml` актуальны

### Version Catalog Issues

1. Убедитесь, что все зависимости используют version catalog:
   ```kotlin
   // ✅ Правильно
   implementation(libs.compose.ui)
   
   // ❌ Неправильно
   implementation("androidx.compose.ui:ui:1.5.0")
   ```

2. Проверьте, что все версии задекларированы в `gradle/libs.versions.toml`

### Navigation Not Working

1. Убедитесь, что routes правильно определены
2. Проверьте, что подграфы подключены в RootNavGraph
3. Проверьте обработку UiEffect.Navigate в app модуле
4. Убедитесь, что Compose Navigation dependency добавлена

### DI Not Working

1. Убедитесь, что @HiltAndroidApp в Application классе
2. Проверьте, что Hilt модули правильно установлены (@InstallIn)
3. Проверьте зависимости в build.gradle.kts (kapt или ksp для Hilt)
4. Убедитесь, что @Inject конструкторы правильные

### Flow Error Handling

Если Flow не обрабатывает ошибки правильно:

1. Убедитесь, что используется `.catch {}` оператор:
   ```kotlin
   flow { ... }
       .catch { exception ->
           emit(Result.Error(mapToAppError(exception)))
       }
   ```

2. Проверьте, что ошибки маппятся в AppError:
   ```kotlin
   NetworkError(...).toAppError()
   ```

3. Убедитесь, что Flow использует правильный dispatcher:
   ```kotlin
   .flowOn(dispatcherProvider.io)
   ```

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

