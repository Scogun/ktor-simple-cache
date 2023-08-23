package com.ucasoft.ktor.simpleMemoryCache

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test

internal class MemoryCacheTests {

    @Test
    fun `test memory cache`() {
        testApplication {
            val jsonClient = client.config {
                install(ContentNegotiation) {
                    json()
                }
            }

            application(Application::testApplication)

            val response = jsonClient.get("short")
            val longResponse = jsonClient.get("long")

            response.shouldHaveStatus(HttpStatusCode.OK)
            longResponse.shouldHaveStatus(HttpStatusCode.OK)

            val firstBody = response.body<TestResponse>()

            val secondResponse = jsonClient.get("short")
            secondResponse.body<TestResponse>().id.shouldBe(firstBody.id)
            Thread.sleep(3000)
            val thirdResponse = jsonClient.get("short")
            thirdResponse.body<TestResponse>().id.shouldNotBe(firstBody.id)

            val secondLongResponse = jsonClient.get("long")
            secondLongResponse.body<TestResponse>().id.shouldBe(longResponse.body<TestResponse>().id)
        }
    }
}