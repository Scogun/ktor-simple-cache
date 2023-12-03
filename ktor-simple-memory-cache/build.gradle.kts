plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21"
}

dependencies {

    implementation(project(":ktor-simple-cache"))

    testImplementation(kotlin("test"))
    testImplementation(ktorServer("test-host"))
    testImplementation(ktorClient("content-negotiation"))
    testImplementation(ktorServer("content-negotiation"))
    testImplementation(ktor("serialization-kotlinx-json"))
    testImplementation(kotest("assertions-core"))
    testImplementation(kotestEx("assertions-ktor", "2.0.0"))
}

kotlin {
    jvmToolchain(11)
}

tasks.test {
    useJUnitPlatform()
}
