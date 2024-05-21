pluginManagement {
    resolutionStrategy {
        plugins {
            val kotlinVersion = "1.9.24"
            kotlin("multiplatform") version kotlinVersion apply false
            kotlin("plugin.serialization") version kotlinVersion apply false
            id("org.jetbrains.kotlinx.kover") version "0.7.6" apply false
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "simple-cache"

include("ktor-simple-cache")

include("ktor-simple-memory-cache")

include("ktor-simple-redis-cache")
