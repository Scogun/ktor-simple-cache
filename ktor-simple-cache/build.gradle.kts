plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") apply false
    id("org.jetbrains.kotlinx.kover")
    id("com.vanniktech.maven.publish")
}

kotlin {
    jvmToolchain(11)
    jvm {
        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
    linuxArm64()
    linuxX64()
    macosArm64()
    mingwX64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(ktorServer("core"))
            }
            kotlin.srcDir("src/main/kotlin")
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(ktorServer("test-host"))
                implementation(kotest("assertions-core"))
                implementation(kotest("assertions-ktor"))
                implementation("org.mockito.kotlin:mockito-kotlin:6.3.0")
            }
            kotlin.srcDir("src/test/kotlin")
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    pom {
        configurePom("Ktor Simple Cache", "Base realization of simple output cache for Ktor server", this)
    }
}