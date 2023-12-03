tasks.wrapper {
    gradleVersion = "8.5"
}

allprojects {

    version = "0.0.1"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}