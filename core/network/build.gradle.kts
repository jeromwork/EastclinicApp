plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.eastclinic.core"
version = "1.0.0"

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:auth-contract"))
    implementation(libs.bundles.network)
    implementation(libs.bundles.coroutines)
    
    // Test dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
