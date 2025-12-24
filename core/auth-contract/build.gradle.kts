plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.eastclinic.core"
version = "1.0.0"

dependencies {
    implementation(project(":core:common"))
}


