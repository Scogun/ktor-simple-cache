tasks.wrapper {
    gradleVersion = "9.2.1"
}

allprojects {

    group = "com.ucasoft.ktor"

    version = "0.57.7"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        reports {
            junitXml.required.set(true)
        }
    }
}