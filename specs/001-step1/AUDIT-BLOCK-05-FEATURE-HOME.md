# Блок 5: Feature:home (эталонная реализация)

## Обзор

Создана полная реализация feature:home как эталон для остальных фич. Включает все три слоя: domain, data, presentation.

## Структура

```
feature/home/
├── domain/
│   ├── repository/HomeRepository.kt
│   └── usecase/GetGreetingUseCase.kt
├── data/
│   ├── repository/HomeRepositoryImpl.kt
│   └── di/HomeDataModule.kt
└── presentation/
    ├── HomeUiState.kt
    ├── HomeUiEvent.kt
    ├── HomeUiEffect.kt
    ├── HomeViewModel.kt
    ├── HomeScreen.kt
    ├── SplashScreen.kt
    ├── SettingsScreen.kt
    └── navigation/
        ├── HomeRoutes.kt
        └── HomeGraph.kt
```

## Domain слой

### HomeRepository (`feature/home/domain/src/main/java/com/eastclinic/home/domain/repository/HomeRepository.kt`)

**Назначение**: Интерфейс репозитория для доменной логики.

**Пример кода**:

```kotlin
package com.eastclinic.home.domain.repository

import com.eastclinic.core.common.Result

interface HomeRepository {
    suspend fun getGreeting(): Result<String>
}
```

**Принципы**:
- ✅ Чистый интерфейс (без Android/Retrofit/Room типов)
- ✅ Использует `Result<T>` из `core:common`
- ✅ Domain-модели (String в данном случае)

### GetGreetingUseCase (`feature/home/domain/src/main/java/com/eastclinic/home/domain/usecase/GetGreetingUseCase.kt`)

**Назначение**: Use case для получения приветствия.

**Пример кода**:

```kotlin
package com.eastclinic.home.domain.usecase

import com.eastclinic.core.async.DispatcherProvider
import com.eastclinic.core.common.Result
import kotlinx.coroutines.withContext

class GetGreetingUseCase(
    private val repository: HomeRepository,
    private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(): Result<String> {
        return withContext(dispatcherProvider.io) {
            repository.getGreeting()
        }
    }
}
```

**Принципы**:
- ✅ Использует `DispatcherProvider` для тестируемости
- ✅ Один use case = одна операция
- ✅ Не знает о UI или data реализации

**build.gradle.kts**:
```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:async"))
    
    testImplementation(libs.bundles.test.unit)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)
}
```

## Data слой

### HomeRepositoryImpl (`feature/home/data/src/main/java/com/eastclinic/home/data/repository/HomeRepositoryImpl.kt`)

**Назначение**: Реализация репозитория (stub на Step 1).

**Пример кода**:

```kotlin
package com.eastclinic.home.data.repository

import com.eastclinic.core.common.Result
import com.eastclinic.home.domain.repository.HomeRepository
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor() : HomeRepository {
    override suspend fun getGreeting(): Result<String> {
        return Result.Success("Welcome to Eastclinic!")
    }
}
```

**Принципы**:
- ✅ Реализует domain интерфейс
- ✅ Stub реализация (без реального API на Step 1)
- ✅ Singleton scope

### HomeDataModule (`feature/home/data/src/main/java/com/eastclinic/home/data/di/HomeDataModule.kt`)

**Назначение**: Hilt модуль для биндинга интерфейса к реализации.

**Пример кода**:

```kotlin
package com.eastclinic.home.data.di

import com.eastclinic.home.data.repository.HomeRepositoryImpl
import com.eastclinic.home.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeDataModule {
    
    @Binds
    abstract fun bindHomeRepository(
        impl: HomeRepositoryImpl
    ): HomeRepository
}
```

**Принципы**:
- ✅ Использует `@Binds` для интерфейсов
- ✅ Singleton scope
- ✅ Модуль в data слое (где реализация)

**build.gradle.kts**:
```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.eastclinic.home.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }
}

dependencies {
    implementation(project(":feature:home:domain"))
    implementation(project(":core:common"))
    
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}
```

## Presentation слой

### UiState/Event/Effect

**HomeUiState.kt**:
```kotlin
package com.eastclinic.home.presentation

import com.eastclinic.core.common.AppError

data class HomeUiState(
    val greeting: String = "",
    val isLoading: Boolean = false,
    val error: AppError? = null
)
```

**HomeUiEvent.kt**:
```kotlin
package com.eastclinic.home.presentation

sealed class HomeUiEvent {
    object LoadGreeting : HomeUiEvent()
    object NavigateToSettings : HomeUiEvent()
}
```

**HomeUiEffect.kt**:
```kotlin
package com.eastclinic.home.presentation

import com.eastclinic.core.ui.UiEffect

sealed class HomeUiEffect : UiEffect {
    data class Navigate(val route: String) : HomeUiEffect()
    data class ShowMessage(val message: String) : HomeUiEffect()
}
```

### HomeViewModel

**Полный код** (`feature/home/presentation/src/main/java/com/eastclinic/home/presentation/HomeViewModel.kt`):

```kotlin
package com.eastclinic.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eastclinic.core.common.Result
import com.eastclinic.home.domain.usecase.GetGreetingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getGreetingUseCase: GetGreetingUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _uiEffect = MutableSharedFlow<HomeUiEffect>()
    val uiEffect: SharedFlow<HomeUiEffect> = _uiEffect.asSharedFlow()
    
    fun handleEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.LoadGreeting -> loadGreeting()
            is HomeUiEvent.NavigateToSettings -> navigateToSettings()
        }
    }
    
    private fun loadGreeting() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getGreetingUseCase()) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(isLoading = false, greeting = result.data) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.error) 
                    }
                    _uiEffect.emit(HomeUiEffect.ShowMessage(result.error.message))
                }
            }
        }
    }
    
    private fun navigateToSettings() {
        viewModelScope.launch {
            _uiEffect.emit(HomeUiEffect.Navigate(HomeRoutes.SETTINGS))
        }
    }
}
```

**Принципы**:
- ✅ Hilt ViewModel (`@HiltViewModel`)
- ✅ StateFlow для состояния
- ✅ SharedFlow для эффектов
- ✅ Использует use case (не репозиторий напрямую)

### Screens

**HomeScreen.kt**:
```kotlin
package com.eastclinic.home.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import com.eastclinic.home.presentation.navigation.HomeRoutes

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val uiEffect = remember { viewModel.uiEffect }
    
    LaunchedEffect(Unit) {
        viewModel.handleEvent(HomeUiEvent.LoadGreeting)
    }
    
    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is HomeUiEffect.Navigate -> onNavigate(effect.route)
                is HomeUiEffect.ShowMessage -> {
                    // Handle message (snackbar/toast)
                }
            }
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = uiState.greeting,
                    modifier = Modifier.testTag("greeting_text")
                )
                Button(onClick = { 
                    viewModel.handleEvent(HomeUiEvent.NavigateToSettings) 
                }) {
                    Text("Go to Settings")
                }
            }
        }
    }
}
```

**SplashScreen.kt** и **SettingsScreen.kt** — аналогичные stub экраны.

### Navigation

**HomeRoutes.kt**:
```kotlin
package com.eastclinic.home.presentation.navigation

object HomeRoutes {
    const val SPLASH = "home/splash"
    const val HOME = "home/home"
    const val SETTINGS = "home/settings"
}
```

**HomeGraph.kt**:
```kotlin
package com.eastclinic.home.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.eastclinic.home.presentation.HomeScreen
import com.eastclinic.home.presentation.HomeUiEvent
import com.eastclinic.home.presentation.SettingsScreen
import com.eastclinic.home.presentation.SplashScreen

fun NavGraphBuilder.homeGraph(
    navController: NavHostController
) {
    composable(HomeRoutes.SPLASH) {
        SplashScreen(
            onNavigateToHome = {
                navController.navigate(HomeRoutes.HOME) {
                    popUpTo(HomeRoutes.SPLASH) { inclusive = true }
                }
            }
        )
    }
    
    composable(HomeRoutes.HOME) {
        HomeScreen(
            onNavigate = { route ->
                navController.navigate(route)
            }
        )
    }
    
    composable(HomeRoutes.SETTINGS) {
        SettingsScreen(
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}
```

**build.gradle.kts**:
```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.eastclinic.home.presentation"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }
    
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":feature:home:domain"))
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:async"))
    
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.android)
}
```

## Зависимости

```
feature:home:presentation
    ├── feature:home:domain
    ├── core:ui
    ├── core:common
    └── core:async

feature:home:data
    └── feature:home:domain
    └── core:common

feature:home:domain
    ├── core:common
    └── core:async
```

**Правила**:
- ✅ Presentation → Domain
- ✅ Data → Domain
- ✅ Domain → Core (только common, async)
- ❌ Нет обратных зависимостей
- ❌ Domain не содержит Android типов

## Коммиты

1. **87bffae** — `feat(home): add domain layer (repository interface, use case)`
2. **68afbc0** — `feat(home): add data layer (repository impl, Hilt module)`
3. **312bca1** — `feat(home): add presentation layer (screens, ViewModel, navigation)`

## Проверка

✅ Все три слоя реализованы  
✅ Clean Architecture соблюдена  
✅ Hilt реально используется  
✅ Навигация работает  
✅ UI паттерн (State/Event/Effect) соблюден

