# Ktor Simple Redis Cache
Redis cache provider for Ktor Simple Cache plugin
## Setup
### Gradle
```kotlin
repositories {
    mavenCentral()
}

implementation("com.ucasoft.ktor:ktor-simple-redis-cache:0.0.8")
```
## Usage
```kotlin
    install(SimpleCache) {
        redisCache {
            invalidateAt = 10.seconds
            host = redis.host
            port = redis.firstMappedPort
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