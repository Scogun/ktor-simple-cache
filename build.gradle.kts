tasks.wrapper {
    gradleVersion = "8.9"
}

allprojects {

    group = "com.ucasoft.ktor"

    version = "0.5.4"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        reports {
            junitXml.required.set(true)
        }
    }
}