tasks.wrapper {
    gradleVersion = "9.4.1"
}

allprojects {

    group = "com.ucasoft.ktor"

    version = "0.63.3"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        reports {
            junitXml.required.set(true)
        }
    }
}