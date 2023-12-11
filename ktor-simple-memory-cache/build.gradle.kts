plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("publish")
}

dependencies {

    implementation(project(":ktor-simple-cache"))
    implementation(coroutines("core"))

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

libraryData {
    name.set("Ktor Simple Memory Cache")
    description.set("Memory cache provider for Simple Cache plugin")
}