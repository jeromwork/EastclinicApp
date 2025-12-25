plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.eastclinic.feature"
version = "1.0.0"

dependencies {
    implementation(project(":core:common"))

    testImplementation(libs.bundles.test.unit)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.vintage.engine)
}


