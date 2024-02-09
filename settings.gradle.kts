pluginManagement {
    resolutionStrategy {
        plugins {
            val kotlinVersion = "1.9.22"
            kotlin("multiplatform") version kotlinVersion
            kotlin("plugin.serialization") version kotlinVersion
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
