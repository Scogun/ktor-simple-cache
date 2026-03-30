pluginManagement {
    resolutionStrategy {
        plugins {
            val kotlinVersion = "2.3.20"
            kotlin("multiplatform") version kotlinVersion apply false
            kotlin("plugin.serialization") version kotlinVersion apply false
            id("org.jetbrains.kotlinx.kover") version "0.9.7" apply false
            id("com.vanniktech.maven.publish") version "0.36.0" apply false
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "simple-cache"

include("ktor-simple-cache")

include("ktor-simple-memory-cache")

include("ktor-simple-redis-cache")
