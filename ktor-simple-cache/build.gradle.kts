plugins {
    kotlin("jvm")
    id("publish")
}

dependencies {

    implementation(ktorServer("core"))

    testImplementation(kotlin("test"))
    testImplementation(ktorServer("test-host"))
    testImplementation(kotest("assertions-core"))
    testImplementation(kotestEx("assertions-ktor", "2.0.0"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
}

kotlin {
    jvmToolchain(11)
}

libraryData {
    name.set("Ktor Simple Cache")
    description.set("Base realization of simple output cache for Ktor server")
}