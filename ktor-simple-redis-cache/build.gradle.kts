plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("publish")
}

kotlin {
    jvm {
        jvmToolchain(11)
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":ktor-simple-cache"))
                implementation("redis.clients:jedis:5.1.0")
                implementation("com.google.code.gson:gson:2.10.1")
            }
            kotlin.srcDir("src/main/kotlin")
        }
        val jvmTest by getting {
            dependencies {
                implementation("com.redis.testcontainers:testcontainers-redis-junit:1.6.4")
                implementation(kotlin("test"))
                implementation(ktorServer("test-host"))
                implementation(ktorClient("content-negotiation"))
                implementation(ktorServer("content-negotiation"))
                implementation(ktor("serialization-kotlinx-json"))
                implementation(kotest("assertions-core"))
                implementation(kotestEx("assertions-ktor", "2.0.0"))
            }
            kotlin.srcDir("src/test/kotlin")
        }
    }
}

libraryData {
    name.set("Ktor Simple Redis Cache")
    description.set("Redis cache provider for Simple Cache plugin")
}