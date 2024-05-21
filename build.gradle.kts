tasks.wrapper {
    gradleVersion = "8.7"
}

allprojects {

    group = "com.ucasoft.ktor"

    version = "0.3.1"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        reports {
            junitXml.required.set(true)
        }
    }
}