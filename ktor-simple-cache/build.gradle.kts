plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") apply false
    id("org.jetbrains.kotlinx.kover")
    id("publish")
}

kotlin {
    jvmToolchain(11)
    jvm {
        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
    linuxX64()
    macosX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(ktorServer("core"))
            }
            kotlin.srcDir("src/main/kotlin")
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(ktorServer("test-host"))
                implementation(kotest("assertions-core"))
                implementation(kotest("assertions-ktor"))
                implementation("org.mockito.kotlin:mockito-kotlin:6.1.0")
            }
            kotlin.srcDir("src/test/kotlin")
        }
    }
}

libraryData {
    name.set("Ktor Simple Cache")
    description.set("Base realization of simple output cache for Ktor server")
}