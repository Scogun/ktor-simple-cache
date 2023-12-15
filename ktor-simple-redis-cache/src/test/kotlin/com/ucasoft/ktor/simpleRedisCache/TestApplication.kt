package com.ucasoft.ktor.simpleRedisCache

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

    routing {
        cacheOutput(2.seconds) {
            get("short") {
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