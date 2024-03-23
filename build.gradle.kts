tasks.wrapper {
    gradleVersion = "8.7"
}

allprojects {

    group = "com.ucasoft.ktor"

    version = "0.2.7"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        reports {
            junitXml.required.set(true)
        }
    }
}