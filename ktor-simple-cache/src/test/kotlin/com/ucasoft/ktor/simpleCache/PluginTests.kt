package com.ucasoft.ktor.simpleCache

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

internal class PluginTests {

    @Test
    fun firstTest() {
        testApplication {
            val jsonClient = client.config {
                install(ContentNegotiation) {
                    json()
                }
            }

            application(Application::testApplication)

            val response = jsonClient.get("folder/firstFile")

            response.shouldHaveStatus(HttpStatusCode.OK)

            val firstBody = response.body<TestResponse>()

            val secondResponse = jsonClient.get("folder/firstFile")
            secondResponse.body<TestResponse>().id.shouldBe(firstBody.id)
            Thread.sleep(5000)
            val thirdResponse = jsonClient.get("folder/firstFile")
            thirdResponse.body<TestResponse>().id.shouldNotBe(firstBody.id)
        }
    }
}