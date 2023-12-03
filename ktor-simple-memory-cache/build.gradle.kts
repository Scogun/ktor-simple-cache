plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21"
    `maven-publish`
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

publishing {
    publications {
        create<MavenPublication>("Simple-Memory-Cache") {
            pom {
                name.set("Ktor Simple Memory Cache")
                description.set("Memory cache provider for Simple Cache plugin")
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