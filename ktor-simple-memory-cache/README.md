# Ktor Simple Memory Cache
Memory cache provider for Ktor Simple Cache plugin
## Setup
### Gradle
```kotlin
repositories {
    mavenCentral()
}

implementation("com.ucasoft.ktor:ktor-simple-memory-cache:0.0.1")
```
## Usage
```kotlin
    install(SimpleCache) {
        memoryCache {
            invalidateAt = 10.seconds
        }
    }

    routing {
        cacheOutput(2.seconds) {
            get("short-cache") {
                call.respond(Random.nextInt())
            }
        }
        cacheOutput {
            get("default-cache") {
                call.respond(Random.nextInt())
            }
        }
    }
```