# Ktor Simple Cache
Base solution which provides the plugin implementation and abstract class for cache providers.
## Setup
### Gradle
```kotlin
repositories {
    mavenCentral()
}

implementation("com.ucasoft.ktor:ktor-simple-cache:0.0.1")
```
## Usage
```kotlin
    install(SimpleCache) {
        cacheProvider {
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