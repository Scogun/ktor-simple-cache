package com.ucasoft.ktor.simpleCache

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import kotlin.time.Duration

class SimpleCachePluginConfig {

    var invalidateAt: Duration? = null
}

val SimpleCachePlugin = createRouteScopedPlugin(name = "SimpleCachePlugin", ::SimpleCachePluginConfig) {
    val provider = application.plugin(SimpleCache).config.provider
    val isResponseFromCacheKey = AttributeKey<Boolean>("isResponseFromCacheKey")
    onCall {
        val cache = provider.getCache(it.request.uri)
        if (cache != null) {
            it.attributes.put(isResponseFromCacheKey, true)
            it.respond(cache)
        }
    }
    onCallRespond { call, body ->
        if (!call.attributes.contains(isResponseFromCacheKey)) {
            provider.setCache(call.request.uri, body, pluginConfig.invalidateAt)
        }
    }
}