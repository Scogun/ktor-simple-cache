package com.ucasoft.ktor.simpleCache

import java.time.LocalDateTime
import kotlin.time.Duration

class SimpleMemoryCacheProvider(config: Config) : SimpleCacheProvider(config) {

    private val cache = mutableMapOf<String, SimpleMemoryCacheObject>()

    override fun getCache(key: String): Any? {
        val `object` = cache[key]
        if (`object` == null || `object`.isExpired) {
            return null
        }

        return `object`.content
    }

    override fun setCache(key: String, content: Any, invalidateAt: Duration?) {
        cache[key] = SimpleMemoryCacheObject(content, invalidateAt ?: this.invalidateAt)
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