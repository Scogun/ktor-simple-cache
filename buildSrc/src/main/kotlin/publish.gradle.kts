import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.`maven-publish`
import org.gradle.kotlin.dsl.signing

plugins {
    `maven-publish`
    signing
}

val libraryData = extensions.create("libraryData", PublishingExtension::class)

val stubJavadoc by tasks.creating(Jar::class) {
    archiveClassifier.set("javadoc")
}

val sourceJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(project.the<JavaPluginExtension>().sourceSets["main"].allSource)
}

publishing {
    publications {
        create<MavenPublication>("SimpleCache") {
            from(project.components["java"])
            artifact(sourceJar)
            artifact(stubJavadoc)
            pom {
                name.set(libraryData.name)
                description.set(libraryData.description)
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
    repositories {
        maven {
            name = "MavenCentral"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

signing {
    sign(publishing.publications)
}