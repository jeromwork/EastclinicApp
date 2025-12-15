plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.eastclinic.core"
version = "1.0.0"

dependencies {
    // Test dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
}
