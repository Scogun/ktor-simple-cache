tasks.wrapper {
    gradleVersion = "8.12.1"
}

allprojects {

    group = "com.ucasoft.ktor"

    version = "0.53.4"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        reports {
            junitXml.required.set(true)
        }
    }
}