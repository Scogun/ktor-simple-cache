plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.kover")
    id("publish")
}

kotlin {
    jvmToolchain(11)
    jvm()
    linuxX64()
    macosX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":ktor-simple-cache"))
            }
            kotlin.srcDir("src/main/kotlin")
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(ktorServer("test-host"))
                implementation(ktorClient("content-negotiation"))
                implementation(ktorServer("content-negotiation"))
                implementation(ktor("serialization-kotlinx-json"))
                implementation(kotest("assertions-core"))
                implementation(kotest("assertions-ktor"))
            }
            kotlin.srcDir("src/test/kotlin")
        }
    }
}

libraryData {
    name.set("Ktor Simple Memory Cache")
    description.set("Memory cache provider for Simple Cache plugin")
}