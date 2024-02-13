tasks.wrapper {
    gradleVersion = "8.6"
}

allprojects {

    group = "com.ucasoft.ktor"

    version = "0.2.3"

    apply {
        plugin("jacoco")
    }

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        reports {
            junitXml.required.set(true)
        }
    }
}