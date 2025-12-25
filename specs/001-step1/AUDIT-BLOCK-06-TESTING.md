# Блок 6: Тестирование

## Обзор

Реализованы smoke-тесты для Step 1: unit тест для ViewModel и Compose UI тест для экрана.

## Тесты

### 1. Unit тест: HomeViewModel

**Файл**: `feature/home/presentation/src/test/java/com/eastclinic/home/presentation/HomeViewModelTest.kt`

**Технологии**:
- JUnit5 (Jupiter)
- MockK (моки)
- Turbine (тестирование Flow)
- kotlinx-coroutines-test

**Пример кода**:

```kotlin
package com.eastclinic.home.presentation

import app.cash.turbine.test
import com.eastclinic.core.async.TestDispatchers
import com.eastclinic.core.common.AppError
import com.eastclinic.core.common.Result
import com.eastclinic.home.domain.usecase.GetGreetingUseCase
import io.mockk.coEvery
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class HomeViewModelTest {
    
    @MockK
    private lateinit var getGreetingUseCase: GetGreetingUseCase
    
    private lateinit var viewModel: HomeViewModel
    
    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = HomeViewModel(getGreetingUseCase)
    }
    
    @Test
    fun `when LoadGreeting event, then state updates with greeting`() = runTest {
        // Given
        val expectedGreeting = "Welcome to Eastclinic!"
        coEvery { getGreetingUseCase() } returns Result.Success(expectedGreeting)
        
        // When
        viewModel.handleEvent(HomeUiEvent.LoadGreeting)
        
        // Then
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(false, initialState.isLoading)
            assertEquals("", initialState.greeting)
            
            val loadingState = awaitItem()
            assertEquals(true, loadingState.isLoading)
            
            val finalState = awaitItem()
            assertEquals(false, finalState.isLoading)
            assertEquals(expectedGreeting, finalState.greeting)
        }
    }
    
    @Test
    fun `when LoadGreeting fails, then error state and effect emitted`() = runTest {
        // Given
        val error = AppError.UnknownError("Test error")
        coEvery { getGreetingUseCase() } returns Result.Error(error)
        
        // When
        viewModel.handleEvent(HomeUiEvent.LoadGreeting)
        
        // Then
        viewModel.uiState.test {
            skipItems(2) // Skip initial and loading states
            val errorState = awaitItem()
            assertEquals(error, errorState.error)
        }
        
        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is HomeUiEffect.ShowMessage)
        }
    }
    
    @Test
    fun `when NavigateToSettings event, then Navigate effect emitted`() = runTest {
        // When
        viewModel.handleEvent(HomeUiEvent.NavigateToSettings)
        
        // Then
        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is HomeUiEffect.Navigate)
            assertEquals(HomeRoutes.SETTINGS, (effect as HomeUiEffect.Navigate).route)
        }
    }
}
```

**Особенности**:
- Использует `runTest` для корутин
- Turbine для тестирования Flow
- MockK для моков use case
- Проверяет StateFlow и SharedFlow

### 2. Compose UI тест: HomeScreen

**Файл**: `app/src/androidTest/java/com/eastclinic/app/HomeScreenTest.kt`

**Технологии**:
- Compose UI Testing (compose-ui-test-junit4)
- JUnit4 (AndroidJUnit4 runner)

**Пример кода**:

```kotlin
package com.eastclinic.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eastclinic.core.ui.theme.EastclinicTheme
import com.eastclinic.home.presentation.HomeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun homeScreen_displaysGreeting() {
        composeTestRule.setContent {
            EastclinicTheme {
                HomeScreen(
                    viewModel = hiltViewModel(),
                    onNavigate = {}
                )
            }
        }
        
        composeTestRule.onNodeWithTag("greeting_text")
            .assertIsDisplayed()
            .assertTextContains("Welcome")
    }
}
```

**Особенности**:
- Использует `testTag` для поиска элементов
- Проверяет отображение текста
- Интеграционный тест (требует Hilt)

## Конфигурация тестов

### Unit тесты (JVM модули)

**build.gradle.kts** (пример для feature:home:presentation):
```kotlin
dependencies {
    testImplementation(libs.bundles.test.unit)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)
}
```

**test-unit bundle** (из libs.versions.toml):
```toml
test-unit = [
    "junit-jupiter",
    "mockk",
    "turbine",
    "kotlinx-coroutines-test"
]
```

### UI тесты (Android модули)

**build.gradle.kts** (app модуль):
```kotlin
dependencies {
    androidTestImplementation(libs.bundles.test.ui)
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.bundles.compose.debug)
}
```

**test-ui bundle**:
```toml
test-ui = [
    "junit-android",
    "compose-ui-test",
    "compose-ui-test-junit4"
]
```

## Запуск тестов

### Локально

```bash
# Все unit тесты
./gradlew test

# Тесты конкретного модуля
./gradlew :feature:home:presentation:test

# UI тесты (требует эмулятор/устройство)
./gradlew :app:connectedAndroidTest
```

### В CI

Тесты запускаются автоматически через GitHub Actions (см. Блок 7: CI/CD).

## Покрытие

**Unit тесты**:
- ✅ HomeViewModel (3 теста)
- ✅ Базовые типы (Result, AppError) — существующие тесты

**UI тесты**:
- ✅ HomeScreen (1 smoke тест)

**Примечание**: На Step 1 покрытие минимальное (smoke), полное покрытие будет добавлено в следующих шагах.

## Коммиты

1. **312bca1** — `feat(home): add presentation layer` (включает unit тест)
2. **d9a3039** — `test: add Compose UI smoke test for Home screen`

## Проверка

✅ Unit тесты используют JUnit5  
✅ UI тесты используют Compose Testing  
✅ Тесты проходят локально  
✅ Тесты интегрированы в CI

