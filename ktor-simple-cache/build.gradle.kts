plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") apply false
    id("publish")
}

kotlin {
    jvm {
        jvmToolchain(11)
    }
    sourceSets {
        val jvmMain by getting {
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
                implementation(kotestEx("assertions-ktor", "2.0.0"))
                implementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
            }
            kotlin.srcDir("src/test/kotlin")
        }
    }
}

libraryData {
    name.set("Ktor Simple Cache")
    description.set("Base realization of simple output cache for Ktor server")
}