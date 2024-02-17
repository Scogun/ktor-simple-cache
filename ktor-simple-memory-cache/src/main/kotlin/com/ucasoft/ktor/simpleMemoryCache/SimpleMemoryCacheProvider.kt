package com.ucasoft.ktor.simpleMemoryCache

import com.ucasoft.ktor.simpleCache.SimpleCacheConfig
import com.ucasoft.ktor.simpleCache.SimpleCacheProvider
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

class SimpleMemoryCacheProvider(config: Config) : SimpleCacheProvider(config) {

    private val cache = mutableMapOf<String, SimpleMemoryCacheObject>()

    override suspend fun getCache(key: String): Any? {
        val `object` = cache[key]
        return if (`object`?.isExpired != false) {
            null
        } else {
            `object`.content
        }
    }


    override suspend fun setCache(key: String, content: Any, invalidateAt: Duration?) {
        cache[key] = SimpleMemoryCacheObject(content, invalidateAt ?: this.invalidateAt)
    }

    class Config internal constructor() : SimpleCacheProvider.Config()
}

private data class SimpleMemoryCacheObject(val content: Any, val duration: Duration, val start: Instant = Clock.System.now()) {

    val isExpired: Boolean
        get() = Clock.System.now() - start > duration
}

fun SimpleCacheConfig.memoryCache(
    configure : SimpleMemoryCacheProvider.Config.() -> Unit
){
    provider = SimpleMemoryCacheProvider(SimpleMemoryCacheProvider.Config().apply(configure))
}