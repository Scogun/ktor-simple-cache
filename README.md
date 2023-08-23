# Ktor Simple Cache

This repository hosts a number of libraries for [Ktor](https://ktor.io/) Server to simply add output cache.

To learn more please refer to the `README`s of individual library.

| README                                                         |
|:---------------------------------------------------------------|
| [ktor-simple-cache](ktor-simple-cache/README.md)               |
| [ktor-simple-memory-cache](ktor-simple-memory-cache/README.md) |
| [ktor-simple-redis-cache](ktor-simple-redis-cache/README.md)   |

## Using in Your Projects

Use one of simple cache provider library to setup cache during server configuration:

```kotlin
install(SimpleCache) {
    //cacheProvider {}
}
```