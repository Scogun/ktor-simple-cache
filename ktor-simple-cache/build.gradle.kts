plugins {
    kotlin("jvm")
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

publishing {
    publications {
        create<MavenPublication>("Simple-Cache") {
            pom {
                name.set("Ktor Simple Cache")
                description.set("Base realization of simple output cache for Ktor server")
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