package com.ucasoft.ktor.simpleMemoryCache

import com.ucasoft.ktor.simpleCache.SimpleCache
import com.ucasoft.ktor.simpleCache.cacheOutput
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@Serializable
data class TestResponse(val id: Int = Random.nextInt())

fun Application.testApplication() {

    install(ContentNegotiation) {
        json()
    }

    install(SimpleCache) {
        memoryCache {
            invalidateAt = 10.seconds
        }
    }

    routing {
        cacheOutput(2.seconds) {
            get("folder/{file}") {
                call.respond(TestResponse())
            }
        }
        cacheOutput {
            get("long") {
                call.respond(TestResponse())
            }
        }
    }
}