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
                implementation(kotestEx("assertions-ktor", "2.0.0"))
            }
            kotlin.srcDir("src/test/kotlin")
        }
    }
}

libraryData {
    name.set("Ktor Simple Memory Cache")
    description.set("Memory cache provider for Simple Cache plugin")
}