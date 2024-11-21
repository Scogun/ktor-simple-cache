tasks.wrapper {
    gradleVersion = "8.11.1"
}

allprojects {

    group = "com.ucasoft.ktor"

    version = "0.50.7"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        reports {
            junitXml.required.set(true)
        }
    }
}