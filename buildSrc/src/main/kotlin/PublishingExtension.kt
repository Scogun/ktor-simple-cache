import org.gradle.api.publish.maven.MavenPom


fun configurePom(name: String, description: String, pom: MavenPom) {
    pom.name.set(name)
    pom.description.set(description)
    pom.url.set("https://github.com/Scogun/ktor-simple-cache")
    pom.licenses {
        license {
            this.name.set("The Apache License, Version 2.0")
            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
        }
    }
    pom.developers {
        developer {
            id.set("Scogun")
            this.name.set("Sergey Antonov")
            email.set("SAntonov@ucasoft.com")
        }
    }
    pom.scm {
        connection.set("scm:git:git://github.com/Scogun/ktor-simple-cache.git")
        developerConnection.set("scm:git:ssh://github.com:Scogun/ktor-simple-cache.git")
        url.set("https://github.com/Scogun/ktor-simple-cache")
    }
}