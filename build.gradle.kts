tasks.wrapper {
    gradleVersion = "8.5"
}

allprojects {

    group = "com.ucasoft.ktor"

    version = "0.0.1"

    apply {
    }

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}