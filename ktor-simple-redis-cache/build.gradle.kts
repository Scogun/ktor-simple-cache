plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
}

dependencies {

    implementation(project(":ktor-simple-cache"))
    implementation("redis.clients:jedis:5.0.2")
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation("com.redis.testcontainers:testcontainers-redis-junit:1.6.4")
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
