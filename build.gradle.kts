tasks.wrapper {
    gradleVersion = "8.5"
}


allprojects {
    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}