package com.ucasoft.ktor.simpleCache

import io.ktor.server.application.*
import io.ktor.util.*
import kotlinx.coroutines.sync.Mutex
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class SimpleCacheConfig {

    var provider: SimpleCacheProvider? = null
}

class SimpleCache(internal var config: SimpleCacheConfig) {

    companion object : BaseApplicationPlugin<Application, SimpleCacheConfig, SimpleCache> {
        override val key: AttributeKey<SimpleCache> = AttributeKey("SimpleCacheHolder")

        override fun install(pipeline: Application, configure: SimpleCacheConfig.() -> Unit): SimpleCache {
            return SimpleCache(SimpleCacheConfig().apply(configure))
        }
    }
}

abstract class SimpleCacheProvider(config: Config) {

    val invalidateAt = config.invalidateAt

    private val mutex = Mutex()

    abstract suspend fun getCache(key: String): Any?
    abstract suspend fun setCache(key: String, content: Any, invalidateAt: Duration?)

    fun handleBadResponse() {
        mutex.unlock()
    }

    suspend fun loadCache(key: String): Any? {
        var cache = getCache(key)
        return if (cache == null) {
            mutex.lock()
            cache = getCache(key)
            if (cache == null) {
                null
            } else {
                mutex.unlock()
                cache
            }
        } else {
            cache
        }
    }

    suspend fun saveCache(key: String, content: Any, invalidateAt: Duration?) {
        setCache(key, content, invalidateAt)
        mutex.unlock()
    }

    open class Config protected constructor() {

         var invalidateAt: Duration = 5.minutes
    }
}
