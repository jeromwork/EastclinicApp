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


