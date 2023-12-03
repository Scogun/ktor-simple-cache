plugins {
    kotlin("jvm") version "1.9.21"
}

dependencies {

    implementation(ktorServer("core"))

    testImplementation(kotlin("test"))
    testImplementation(ktorServer("test-host"))
    testImplementation(kotest("assertions-core"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}

kotlin {
    jvmToolchain(11)
}

