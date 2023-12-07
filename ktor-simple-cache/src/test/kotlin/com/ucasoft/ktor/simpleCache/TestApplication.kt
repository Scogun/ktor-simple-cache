package com.ucasoft.ktor.simpleCache

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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

fun Application.testApplicationWithKey() {
    routing {
        cacheOutput(queryKeys = listOf("param1")) {
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