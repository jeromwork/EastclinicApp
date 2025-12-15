pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "EastclinicApp"

// Core modules will be registered here as they are created
include(":core:common")
include(":core:async")
include(":core:ui")
include(":core:auth-contract")
include(":core:push-contract")
include(":core:network")

// App module will be registered here
include(":app")

// Feature modules will be registered here as they are created
include(":feature:auth:presentation")
include(":feature:auth:domain")
include(":feature:auth:data")
include(":feature:home:presentation")
include(":feature:home:domain")
include(":feature:home:data")
include(":feature:clinics:presentation")
include(":feature:clinics:domain")
include(":feature:clinics:data")
include(":feature:doctors:presentation")
include(":feature:doctors:domain")
include(":feature:doctors:data")
include(":feature:appointments:presentation")
include(":feature:appointments:domain")
include(":feature:appointments:data")
include(":feature:chat:presentation")
include(":feature:chat:domain")
include(":feature:chat:data")
