plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21"
    `maven-publish`
}

dependencies {

    implementation(project(":ktor-simple-cache"))
    implementation("redis.clients:jedis:5.1.0")
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

publishing {
    publications {
        create<MavenPublication>("Simple-Redis-Cache") {
            pom {
                name.set("Ktor Simple Redis Cache")
                description.set("Redis cache provider for Simple Cache plugin")
                url.set("https://github.com/Scogun/ktor-simple-cache")
                licenses {
                    license {
                        name.set("GPL-3.0 License")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    }
                }
                developers {
                    developer {
                        id.set("Scogun")
                        name.set("Sergey Antonov")
                        email.set("SAntonov@ucasoft.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Scogun/ktor-simple-cache.git")
                    developerConnection.set("scm:git:ssh://github.com:Scogun/ktor-simple-cache.git")
                    url.set("https://github.com/Scogun/ktor-simple-cache")
                }
            }
        }
    }
}