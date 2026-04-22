package com.ucasoft.ktor.simpleRedisCache

import com.ucasoft.ktor.simpleCache.SimpleCacheConfig
import com.ucasoft.ktor.simpleCache.SimpleCacheProvider
import io.github.domgew.kedis.KedisClient
import io.github.domgew.kedis.arguments.value.SetOptions
import io.github.domgew.kedis.commands.KedisValueCommands
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class SimpleRedisCacheProvider(config: Config) : SimpleCacheProvider(config) {

    private val jedis = KedisClient.builder {
        hostAndPort(config.host, config.port)
        connectTimeout = 250.milliseconds
    }

    override suspend fun getCache(key: String): Any? =
        jedis.execute(KedisValueCommands.get(key))?.let { Json.decodeFromString<CachedResponse>(it).toOutgoingContent() }

    override suspend fun setCache(key: String, content: Any, invalidateAt: Duration?) {
        val expired = (invalidateAt ?: this.invalidateAt).inWholeMilliseconds
        val outgoing = content as OutgoingContent
        jedis.execute(KedisValueCommands.set(key, Json.encodeToString(CachedResponse(
            bytes = outgoing.toByteArray(),
            contentType = outgoing.contentType?.toString(),
            status = outgoing.status?.value,
            contentLength = outgoing.contentLength
        )), SetOptions(expire = SetOptions.ExpireOption.ExpiresInMilliseconds(expired))))
    }

    class Config internal constructor() : SimpleCacheProvider.Config() {

        var host = "localhost"

        var port = 6379
    }
}

@Serializable
private data class CachedResponse(
    val bytes: ByteArray,
    val contentType: String?,
    val status: Int?,
    val contentLength: Long?
) {

    fun toOutgoingContent() = object : OutgoingContent.ByteArrayContent() {
        override fun bytes() = bytes
        override val contentType = this@CachedResponse.contentType?.let { ContentType.parse(it) }
        override val status = this@CachedResponse.status?.let { HttpStatusCode.fromValue(it) }
        override val contentLength = this@CachedResponse.contentLength
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as CachedResponse

        if (status != other.status) return false
        if (contentLength != other.contentLength) return false
        if (!bytes.contentEquals(other.bytes)) return false
        if (contentType != other.contentType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status ?: 0
        result = 31 * result + (contentLength?.hashCode() ?: 0)
        result = 31 * result + bytes.contentHashCode()
        result = 31 * result + (contentType?.hashCode() ?: 0)
        return result
    }
}

fun SimpleCacheConfig.redisCache(
    configure : SimpleRedisCacheProvider.Config.() -> Unit
){
    provider = SimpleRedisCacheProvider(SimpleRedisCacheProvider.Config().apply(configure))
}

private suspend fun OutgoingContent.toByteArray(): ByteArray = when (this) {
    is OutgoingContent.ByteArrayContent -> bytes()
    is OutgoingContent.NoContent -> byteArrayOf()
    is OutgoingContent.ReadChannelContent -> readFrom().readRemaining().readByteArray()
    is OutgoingContent.WriteChannelContent -> {
        val channel = ByteChannel(autoFlush = true)
        writeTo(channel)
        channel.close()
        channel.readRemaining().readByteArray()
    }
    else -> byteArrayOf()
}