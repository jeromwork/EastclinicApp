plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.eastclinic.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.eastclinic.app"
        minSdk = 26
        targetSdk = 34
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
    
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    
    // Feature modules
    implementation(project(":feature:auth:presentation"))
    implementation(project(":feature:home:presentation"))
    implementation(project(":feature:clinics:presentation"))
    implementation(project(":feature:doctors:presentation"))
    implementation(project(":feature:appointments:presentation"))
    implementation(project(":feature:chat:presentation"))
    
    // Test dependencies
    testImplementation(libs.bundles.test.unit)
    androidTestImplementation(libs.bundles.test.ui)
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.bundles.compose.debug)
}


