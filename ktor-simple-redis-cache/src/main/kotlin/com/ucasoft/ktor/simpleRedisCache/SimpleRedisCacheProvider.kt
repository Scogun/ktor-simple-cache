package com.ucasoft.ktor.simpleRedisCache

import com.ucasoft.ktor.simpleCache.SimpleCacheConfig
import com.ucasoft.ktor.simpleCache.SimpleCacheProvider
import redis.clients.jedis.JedisPooled
import kotlin.time.Duration

class SimpleRedisCacheProvider(private val config: Config) : SimpleCacheProvider(config) {

    private val jedis: JedisPooled = JedisPooled(config.host, config.port, config.ssl)

    override fun getCache(key: String): Any? = if (jedis.exists(key)) jedis.jsonGet(key) else null

    override fun setCache(key: String, content: Any, invalidateAt: Duration?) {
        jedis.jsonSet(key, content)
        jedis.pexpire(key, (invalidateAt ?: config.invalidateAt).inWholeMilliseconds)
    }

    class Config internal constructor(): SimpleCacheProvider.Config() {

        var host = "localhost"

        var port = 6379

        var ssl = false
    }
}

fun SimpleCacheConfig.redisCache(
    configure : SimpleRedisCacheProvider.Config.() -> Unit
){
    provider = SimpleRedisCacheProvider(SimpleRedisCacheProvider.Config().apply(configure))
}