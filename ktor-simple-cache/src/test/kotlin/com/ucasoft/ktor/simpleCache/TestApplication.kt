package com.ucasoft.ktor.simpleCache

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.time.Duration.Companion.minutes

fun Application.badTestApplication() {

    install(SimpleCache) {
    }

    routing {
        cacheOutput {
            get("/check") {
                call.respondText("Check response")
            }
        }
    }
}

fun Application.testApplication() {

    routing {
        cacheOutput {
            get("/check") {
                call.respondText("Check response")
            }
        }
    }
}

fun SimpleCacheConfig.testCache(
    testProvider : SimpleCacheProvider
){
    provider = testProvider
}