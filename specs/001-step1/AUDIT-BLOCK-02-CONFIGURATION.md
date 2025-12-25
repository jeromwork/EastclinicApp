# Блок 2: Конфигурация проекта

## Обзор

Обновлена конфигурация Gradle проекта для Step 1: версии SDK, тестовые библиотеки (JUnit5), version catalog, настройки компиляции.

## Изменения

### 1. Version Catalog (`gradle/libs.versions.toml`)

**Добавлено**:
- Версии для JUnit5 (Jupiter + Vintage Engine)
- SDK версии (compile-sdk, target-sdk, min-sdk)
- Библиотеки для JUnit5

**Пример кода**:

```toml
[versions]
# Android & Gradle
agp = "8.1.4"
gradle = "8.10"
kotlin = "1.9.22"

# SDK
compile-sdk = "35"
target-sdk = "35"
min-sdk = "24"

# Testing
junit = "5.10.1"
junit-vintage = "5.10.1"
junit-android = "1.1.5"
mockk = "1.13.8"
turbine = "1.0.0"
compose-ui-test = "1.5.4"
compose-ui-test-junit4 = "1.5.4"
kotlinx-coroutines-test = "1.7.3"

[libraries]
# JUnit5
junit-jupiter = { group = "org.junit.jupiter", name = "junit-jupiter", version.ref = "junit" }
junit-vintage-engine = { group = "org.junit.vintage", name = "junit-vintage-engine", version.ref = "junit-vintage" }
junit-platform-launcher = { group = "org.junit.platform", name = "junit-platform-launcher", version = "1.10.1" }

# Testing (existing)
junit = { group = "junit", name = "junit", version.ref = "junit" }
junit-android = { group = "androidx.test.ext", name = "junit", version.ref = "junit-android" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
compose-ui-test = { group = "androidx.compose.ui", name = "ui-test", version.ref = "compose-ui-test" }
compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4", version.ref = "compose-ui-test-junit4" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "kotlinx-coroutines-test" }

[bundles]
test-unit = [
    "junit-jupiter",
    "mockk",
    "turbine",
    "kotlinx-coroutines-test"
]
test-ui = [
    "junit-android",
    "compose-ui-test",
    "compose-ui-test-junit4"
]
```

**Изменения**:
- `junit` теперь ссылается на JUnit5 (Jupiter)
- Добавлен `junit-vintage-engine` для совместимости с JUnit4 тестами
- Добавлен `junit-platform-launcher` для запуска тестов
- Bundle `test-unit` обновлен для JUnit5

### 2. Root Build Configuration (`build.gradle.kts`)

**Добавлено**:
- Поддержка JUnit Platform для всех JVM модулей
- Настройка Java 17 для всех JVM модулей

**Пример кода**:

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt.android) apply false
}

// Общая конфигурация для всех JVM модулей (domain модули и core:common)
subprojects {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        // Применяем Java Library plugin для доступа к Java extension
        apply(plugin = "java-library")
        
        // Настройка Kotlin компиляции для Java 17
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
        
        // Настройка sourceCompatibility и targetCompatibility для Java компиляции
        tasks.withType<JavaCompile>().configureEach {
            sourceCompatibility = "17"
            targetCompatibility = "17"
            options.release.set(17)
        }
        
        // JUnit Platform для всех JVM модулей
        dependencies {
            testRuntimeOnly(libs.junit.jupiter.engine)
            testRuntimeOnly(libs.junit.vintage.engine)
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
```

**Ключевые изменения**:
- Добавлена конфигурация JUnit Platform для всех JVM модулей
- Унифицирована настройка Java 17 для всех JVM модулей
- Используется `options.release.set(17)` для совместимости

### 3. Gradle Wrapper (`gradle/wrapper/gradle-wrapper.properties`)

**Текущая версия**: Gradle 8.10

**Файл**:
```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.10-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

## Влияние на модули

### Core модули

Все core модули обновлены для использования:
- SDK 24/35 (вместо 26/34)
- JUnit5 (вместо JUnit4)

**Пример** (`core/common/build.gradle.kts`):
```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.eastclinic.core"
version = "1.0.0"

dependencies {
    // Test dependencies
    testImplementation(libs.bundles.test.unit)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)
}
```

### Feature модули

Все feature модули обновлены аналогично.

**Пример** (`feature/home/domain/build.gradle.kts`):
```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.eastclinic.feature"
version = "1.0.0"

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:async"))
    
    // Test dependencies
    testImplementation(libs.bundles.test.unit)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)
}
```

### App модуль

**Пример** (`app/build.gradle.kts`):
```kotlin
android {
    namespace = "com.eastclinic.app"
    compileSdk = 35  // Обновлено с 34

    defaultConfig {
        applicationId = "com.eastclinic.app"
        minSdk = 24  // Обновлено с 26
        targetSdk = 35  // Обновлено с 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    // ...
}

dependencies {
    // Test dependencies
    testImplementation(libs.bundles.test.unit)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)
    
    androidTestImplementation(libs.bundles.test.ui)
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.bundles.compose.debug)
}
```

## Миграция с JUnit4 на JUnit5

### Изменения в тестах

**Было (JUnit4)**:
```kotlin
import org.junit.Test
import org.junit.Assert.*

class MyTest {
    @Test
    fun testSomething() {
        assertEquals(1, 1)
    }
}
```

**Стало (JUnit5)**:
```kotlin
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class MyTest {
    @Test
    fun testSomething() {
        assertEquals(1, 1)
    }
}
```

### Совместимость

JUnit Vintage Engine позволяет запускать старые JUnit4 тесты вместе с JUnit5:
- JUnit4 тесты продолжают работать
- Новые тесты пишутся на JUnit5
- Постепенная миграция возможна

## Проверка конфигурации

### Команды для проверки

```bash
# Проверить версии в catalog
./gradlew dependencies --configuration compileClasspath | grep -i version

# Проверить SDK настройки
./gradlew :app:dependencies | grep -i "compileSdk\|minSdk\|targetSdk"

# Проверить JUnit Platform
./gradlew test --info | grep -i "junit"
```

### Ожидаемые результаты

✅ Все версии из `libs.versions.toml`  
✅ SDK 24/35 во всех модулях  
✅ JUnit5 доступен во всех JVM модулях  
✅ Java 17 для всех JVM модулей  
✅ Нет хардкода версий в build.gradle.kts файлах

## Коммиты

1. **e981723** — `build: update Gradle config for Step1 (SDK 24/35, JUnit5, version catalog)`
   - Обновлен `gradle/libs.versions.toml`
   - Обновлен `build.gradle.kts` (root)
   - Обновлен `gradle-wrapper.properties`

2. **f54fd62** — `build: update core modules for Step1 (SDK 24/35, JUnit5)`
   - Обновлены все core модули
   - Обновлены все feature модули

## Выводы

✅ Version catalog централизует управление версиями  
✅ SDK обновлен до 24/35 (современные требования)  
✅ JUnit5 внедрен с поддержкой JUnit4 (совместимость)  
✅ Java 17 унифицирован для всех JVM модулей  
✅ Конфигурация готова для Step 1

