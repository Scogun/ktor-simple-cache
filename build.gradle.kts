tasks.wrapper {
    gradleVersion = "9.5.1"
}

allprojects {

    group = "com.ucasoft.ktor"

    version = "0.70.2"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        reports {
            junitXml.required.set(true)
        }
    }
}