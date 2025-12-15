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
        // Используем Gradle JDK напрямую, а не toolchain
        tasks.withType<JavaCompile>().configureEach {
            sourceCompatibility = "17"
            targetCompatibility = "17"
            // Используем Java из Gradle JDK (настроен в Android Studio)
            options.release.set(17)
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
