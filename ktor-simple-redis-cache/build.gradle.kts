plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.kover")
    id("com.vanniktech.maven.publish")
}

kotlin {
    jvmToolchain(17)
    jvm {
        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":ktor-simple-cache"))
                implementation("redis.clients:jedis:7.4.0")
                implementation("com.google.code.gson:gson:2.13.2")
            }
            kotlin.srcDir("src/main/kotlin")
        }
        val jvmTest by getting {
            dependencies {
                implementation("com.redis:testcontainers-redis:2.2.4")
                implementation("org.testcontainers:junit-jupiter:1.21.4")
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

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    pom {
        configurePom("Ktor Simple Redis Cache", "Redis cache provider for Simple Cache plugin", this)
    }
}