package com.ucasoft.ktor.simpleRedisCache

import com.redis.testcontainers.RedisContainer
import com.redis.testcontainers.RedisContainer.DEFAULT_IMAGE_NAME
import com.ucasoft.ktor.simpleCache.SimpleCache
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.time.Duration.Companion.seconds

@Testcontainers
internal class RedisCacheTests {

    @Test
    fun `check cache is concurrency`() {
        testApplication {
            val jsonClient = client.config {
                install(ContentNegotiation) {
                    json()
                }
            }

            install(SimpleCache) {
                redisCache {
                    invalidateAt = 10.seconds
                    this.host = redisContainer.host
                    this.port = redisContainer.firstMappedPort
                }
            }

            application(Application::testApplication)

            runBlocking {
                val totalThreads = 1000
                val deferred = (1..totalThreads).map {
                    async {
                        jsonClient.get("/long")
                    }
                }

                val result = deferred.awaitAll().map { it.body<TestResponse>() }.groupBy { it.id }
                    .map { it.key to it.value.size }
                result.shouldBeSingleton {
                    it.second.shouldBe(totalThreads)
                }
            }
        }
    }

    @Test
    fun `test redis cache`() {
        testApplication {
            val jsonClient = client.config {
                install(ContentNegotiation) {
                    json()
                }
            }

            install(SimpleCache) {
                redisCache {
                    invalidateAt = 10.seconds
                    this.host = redisContainer.host
                    this.port = redisContainer.firstMappedPort
                }
            }

            application(Application::testApplication)

            val response = jsonClient.get("/short")
            val longResponse = jsonClient.get("/long")

            response.shouldHaveStatus(HttpStatusCode.OK)
            longResponse.shouldHaveStatus(HttpStatusCode.OK)


            val firstBody = response.body<TestResponse>()

            val secondResponse = jsonClient.get("/short")
            secondResponse.body<TestResponse>().id.shouldBe(firstBody.id)
            Thread.sleep(3000)
            val thirdResponse = jsonClient.get("/short")
            thirdResponse.body<TestResponse>().shouldNotBe(firstBody.id)

            val secondLongResponse = jsonClient.get("/long")
            secondLongResponse.body<TestResponse>().id.shouldBe(longResponse.body<TestResponse>().id)
        }
    }

    @Test
    fun `test content type`() {
        testApplication {
            install(SimpleCache) {
                redisCache {
                    invalidateAt = 10.seconds
                    this.host = redisContainer.host
                    this.port = redisContainer.firstMappedPort
                }
            }

            application(Application::testApplication)

            val textResponse1 = client.get("/text")
            textResponse1.status.shouldBe(HttpStatusCode.OK)
            textResponse1.contentType()?.withoutParameters().shouldBe(ContentType.Text.Plain)

            val textResponse2 = client.get("/text")
            textResponse2.status shouldBe HttpStatusCode.OK
            textResponse2.contentType()?.withoutParameters().shouldBe(ContentType.Text.Plain)
            textResponse2.bodyAsText().shouldBe(textResponse1.bodyAsText())
        }
    }

    companion object {

        @Container
        val redisContainer = RedisContainer(DEFAULT_IMAGE_NAME)
    }
}