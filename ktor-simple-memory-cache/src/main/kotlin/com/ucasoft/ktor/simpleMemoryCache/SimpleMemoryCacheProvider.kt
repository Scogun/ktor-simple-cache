package com.ucasoft.ktor.simpleMemoryCache

import com.ucasoft.ktor.simpleCache.SimpleCacheConfig
import com.ucasoft.ktor.simpleCache.SimpleCacheProvider
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlinx.coroutines.sync.Mutex

class SimpleMemoryCacheProvider(config: Config) : SimpleCacheProvider(config) {

    private val cache = mutableMapOf<String, SimpleMemoryCacheObject>()

    private val mutex = Mutex()

    override suspend fun getCache(key: String): Any? {
        var `object` = cache[key]
        if (`object`?.isExpired != false) {
            mutex.lock()
            `object` = cache[key]
            if (`object`?.isExpired != false) {
                return null
            } else {
                mutex.unlock()
                return `object`.content
            }
        }

        return `object`.content
    }

    override suspend fun setCache(key: String, content: Any, invalidateAt: Duration?) {
        cache[key] = SimpleMemoryCacheObject(content, invalidateAt ?: this.invalidateAt)
        mutex.unlock()
    }

    class Config internal constructor() : SimpleCacheProvider.Config()
}

private data class SimpleMemoryCacheObject(val content: Any, val duration: Duration, val start: LocalDateTime = LocalDateTime.now()) {

    val isExpired: Boolean
        get() = LocalDateTime.now().isAfter(start.plusSeconds(duration.inWholeSeconds))
}

fun SimpleCacheConfig.memoryCache(
    configure : SimpleMemoryCacheProvider.Config.() -> Unit
){
    provider = SimpleMemoryCacheProvider(SimpleMemoryCacheProvider.Config().apply(configure))
}