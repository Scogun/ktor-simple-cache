package com.ucasoft.ktor.simpleRedisCache

import com.google.gson.Gson
import com.ucasoft.ktor.simpleCache.SimpleCacheConfig
import com.ucasoft.ktor.simpleCache.SimpleCacheProvider
import io.github.domgew.kedis.KedisClient
import io.github.domgew.kedis.KedisConfiguration
import io.github.domgew.kedis.arguments.SetOptions
import kotlin.time.Duration

class SimpleRedisCacheProvider(config: Config) : SimpleCacheProvider(config) {

    private val kedis = KedisClient.newClient(
        KedisConfiguration(
            KedisConfiguration.Endpoint.HostPort(config.host, config.port),
            KedisConfiguration.Authentication.NoAutoAuth,
            connectionTimeoutMillis = 250
        )
    )

    override suspend fun getCache(key: String): Any? = if (kedis.exists(key) == 1L) SimpleRedisCacheObject.fromCache(kedis.get(key)!!) else null

    override suspend fun setCache(key: String, content: Any, invalidateAt: Duration?) {
        val expired = (invalidateAt ?: this.invalidateAt).inWholeMilliseconds
        kedis.set(key, SimpleRedisCacheObject.fromObject(content).toString(), options = SetOptions(
            expire = SetOptions.ExpireOption.ExpiresInMilliseconds(expired)
        ))
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