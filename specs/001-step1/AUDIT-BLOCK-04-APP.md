# Блок 4: App модуль

## Обзор

Обновлен app модуль для Step 1: SDK 24/35, DI wiring, упрощенная навигация (только home feature).

## Изменения

### 1. Build Configuration (`app/build.gradle.kts`)

**Изменения**:
- SDK: minSdk=24, compileSdk/targetSdk=35 (было 26/34)
- JUnit5 для тестов
- Зависимости только на `core:*` и `feature:home:presentation`
- Убраны зависимости от других feature модулей

**Пример кода**:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.eastclinic.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.eastclinic.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.navigation.compose)
    kapt(libs.hilt.compiler)
    
    // Core modules
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:async"))
    
    // Feature modules (только home на Step 1)
    implementation(project(":feature:home:presentation"))
    
    // Test dependencies
    testImplementation(libs.bundles.test.unit)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)
    
    androidTestImplementation(libs.bundles.test.ui)
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.bundles.compose.debug)
}
```

### 2. DI Module (`app/src/main/java/com/eastclinic/app/di/AppModule.kt`)

**Назначение**: App-level DI wiring.

**Пример кода**:

```kotlin
package com.eastclinic.app.di

import com.eastclinic.core.async.DefaultDispatcherProvider
import com.eastclinic.core.async.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return DefaultDispatcherProvider()
    }
}
```

**Объяснение**:
- Биндит `DispatcherProvider` для использования во всех feature модулях
- Production реализация (`DefaultDispatcherProvider`)
- Singleton scope для всего приложения

### 3. Navigation (`app/src/main/java/com/eastclinic/app/navigation/RootNavGraph.kt`)

**Назначение**: Корневой граф навигации, композиция feature подграфов.

**Изменения**:
- Упрощен до только home-графа
- Убраны зависимости от других feature модулей
- Стартовая точка: `HomeRoutes.SPLASH`

**Пример кода**:

```kotlin
package com.eastclinic.app.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.eastclinic.home.presentation.navigation.HomeRoutes
import com.eastclinic.home.presentation.navigation.homeGraph

@Composable
fun RootNavGraph(
    navController: NavHostController,
    startDestination: String = HomeRoutes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        homeGraph(navController = navController)
    }
}
```

**Было** (до Step 1):
```kotlin
NavHost(...) {
    composable(RootRoutes.ROOT) { RootScreen() }
    authGraph(...)
    composable(RootRoutes.HOME) { HomeScreen() }
    composable(RootRoutes.CLINICS) { ClinicsScreen() }
    // ... другие feature
}
```

**Стало** (Step 1):
```kotlin
NavHost(...) {
    homeGraph(navController = navController)
}
```

### 4. MainActivity (`app/src/main/java/com/eastclinic/app/MainActivity.kt`)

**Текущая реализация** (без изменений):

```kotlin
package com.eastclinic.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.eastclinic.app.navigation.RootNavGraph
import com.eastclinic.core.ui.theme.EastclinicTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EastclinicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    RootNavGraph(navController = navController)
                }
            }
        }
    }
}
```

## Структура зависимостей

```
app
├── core:common
├── core:ui
├── core:async
└── feature:home:presentation
    ├── feature:home:domain
    └── feature:home:data
```

**Правила**:
- ✅ App зависит только от `core:*` и `feature:*:presentation`
- ✅ App НЕ зависит напрямую от `feature:*:domain` или `feature:*:data`
- ✅ Навигация композируется из feature подграфов

## Коммит

**ID**: `7dc5feb`  
**Сообщение**: `feat(app): update for Step1 (SDK 24/35, DI wiring, home navigation)`  
**Файлов**: 3  
**Изменений**: +51 / -81

## Проверка

✅ SDK обновлен до 24/35  
✅ DI реально используется (DispatcherProvider биндинг)  
✅ Навигация упрощена (только home)  
✅ Зависимости корректны (нет лишних feature)  
✅ JUnit5 настроен

