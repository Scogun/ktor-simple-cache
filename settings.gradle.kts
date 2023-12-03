plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "com.ucasoft.ktor"

include("ktor-simple-cache")

include("ktor-simple-memory-cache")

include("ktor-simple-redis-cache")
