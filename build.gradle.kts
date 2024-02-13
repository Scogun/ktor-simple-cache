plugins {
    id("jacoco-report-aggregation")
}

tasks.wrapper {
    gradleVersion = "8.6"
}

allprojects {

    group = "com.ucasoft.ktor"

    version = "0.2.0"

    apply {
        plugin("jacoco")
    }

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        reports {
            junitXml.required.set(true)
        }
    }

    jacoco {
        toolVersion = "0.8.11"
    }
}

dependencies {
    jacocoAggregation(project(":ktor-simple-cache"))
    jacocoAggregation(project(":ktor-simple-memory-cache"))
    jacocoAggregation(project(":ktor-simple-redis-cache"))
}

reporting {
    reports {
        val codeCoverageReport by creating(JacocoCoverageReport::class) {
            testType.set(TestSuiteType.UNIT_TEST)
        }
    }
}