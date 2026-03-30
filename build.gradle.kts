tasks.wrapper {
    gradleVersion = "9.4.1"
}

allprojects {

    group = "com.ucasoft.ktor"

    version = "0.59.4"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        reports {
            junitXml.required.set(true)
        }
    }
}