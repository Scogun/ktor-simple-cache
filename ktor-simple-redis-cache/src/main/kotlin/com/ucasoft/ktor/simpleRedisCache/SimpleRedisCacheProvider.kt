package com.ucasoft.ktor.simpleRedisCache

import com.google.gson.Gson
import com.ucasoft.ktor.simpleCache.SimpleCacheConfig
import com.ucasoft.ktor.simpleCache.SimpleCacheProvider
import redis.clients.jedis.JedisPooled
import kotlin.time.Duration

class SimpleRedisCacheProvider(private val config: Config) : SimpleCacheProvider(config) {

    private val jedis: JedisPooled = JedisPooled(config.host, config.port, config.ssl)

    override fun getCache(key: String): Any? = if (jedis.exists(key)) SimpleRedisCacheObject.fromCache(jedis.get(key)) else null

    override fun setCache(key: String, content: Any, invalidateAt: Duration?) {
        val expired = (invalidateAt ?: config.invalidateAt).inWholeMilliseconds
        jedis.psetex(key, expired, SimpleRedisCacheObject.fromObject(content).toString())
    }

    class Config internal constructor(): SimpleCacheProvider.Config() {

        var host = "localhost"

        var port = 6379

        var ssl = false
    }
}

private class SimpleRedisCacheObject(val type: String, val content: String) {

    override fun toString() = "$type%#%$content"

    companion object {

        fun fromObject(`object`: Any) = SimpleRedisCacheObject(`object`::class.java.name, Gson().toJson(`object`))

        fun fromCache(cache: String): Any {
            val data = cache.split("%#%")
            return Gson().fromJson(data.last(), Class.forName(data.first()))
        }
    }
}

fun SimpleCacheConfig.redisCache(
    configure : SimpleRedisCacheProvider.Config.() -> Unit
){
    provider = SimpleRedisCacheProvider(SimpleRedisCacheProvider.Config().apply(configure))
}