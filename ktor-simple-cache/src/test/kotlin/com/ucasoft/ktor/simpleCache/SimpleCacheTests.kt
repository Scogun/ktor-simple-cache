package com.ucasoft.ktor.simpleCache

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import org.junit.jupiter.api.Test
import org.mockito.Answers
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

internal class SimpleCacheTests {

    @Test
    fun `no any provider installed`() {
        testApplication {

            application(Application::badTestApplication)

            val exception = shouldThrow<IllegalStateException> {
                client.get("/check")
            }

            exception.message.shouldBe("Add one cache provider!")
        }
    }

    @Test
    fun `check cache call no set`() {
        testApplication {
            val provider = buildProvider(mutableMapOf("/check" to TextContent("Test", ContentType.Any)))

            install(SimpleCache) {
                testCache(provider)
            }

            application(Application::testApplication)

            val response = client.get("/check")
            response.readRawBytes().toString(Charsets.UTF_8).shouldBe("Test")

            verify(provider, times(1)).getCache(eq("/check"))
            verify(provider, never()).setCache(anyString(), any(), anyOrNull())
        }
    }

    @Test
    fun `check bad response not cached`() {
        testApplication {
            val cache = mutableMapOf<String, Any>()
            val provider = buildProvider(cache)

            install(SimpleCache) {
                testCache(provider)
            }

            application(Application::testApplication)

            for (i in 1..2) {
                val response = client.get("/bad")
                response.shouldHaveStatus(HttpStatusCode.BadRequest)
                verify(provider, times(2 * i)).getCache(eq("/bad"))
                verify(provider, times(1 * i)).badResponse()
                verify(provider, times(0)).setCache(eq("/bad"), any(), anyOrNull())
                cache.shouldBeEmpty()
            }
        }
    }

    @Test
    fun `check cache work`() {
        testApplication {
            val cache = mutableMapOf<String, Any>()
            val provider = buildProvider(cache)

            install(SimpleCache) {
                testCache(provider)
            }

            application(Application::testApplication)

            for (i in 0..1) {
                val response = client.get("/check")
                response.readRawBytes().toString(Charsets.UTF_8).toIntOrNull().shouldNotBeNull()
                verify(provider, times(2 + i)).getCache(eq("/check"))
                verify(provider, times(1)).setCache(eq("/check"), any(), anyOrNull())
                cache.shouldNotBeEmpty()
            }
        }
    }

    @Test
    fun `check cache is concurrency`() {
        testApplication {
            val provider = buildProvider()

            install(SimpleCache) {
                testCache(provider)
            }

            application(Application::testApplication)

            runBlocking {
                val totalThreads = 1000
                val deferred = (1..totalThreads).map {
                    if (it == 100) {
                        delay(500)
                    }
                    async {
                        client.get("/check")
                    }
                }

                val result = deferred.awaitAll().map { it.bodyAsText().toInt() }.groupBy { it }
                    .map { it.key to it.value.size }
                result.shouldBeSingleton {
                    it.second.shouldBe(totalThreads)
                }

                verify(provider, atMost(1100)).getCache(eq("/check"))
                verify(provider, times(1)).setCache(eq("/check"), any(), anyOrNull())
            }
        }
    }

    @Test
    fun `check bad responses aren not locked`() {

        testApplication {

            install(SimpleCache) {
                testCache(buildProvider())
            }

            application(Application::testApplication)

            runBlocking {
                val totalThreads = 100
                val deferred = (1..totalThreads).map {
                    async {
                        client.get("/bad")
                    }
                }

                val result = deferred.awaitAll().map { it.bodyAsText() }.groupBy { it }
                    .map { it.key to it.value.size }
                result.shouldBeSingleton {
                    it.second.shouldBe(totalThreads)
                }
            }
        }
    }

    @Test
    fun `check route thrown exceptions aren not locked`() {
        testApplication {

            install(SimpleCache) {
                testCache(buildProvider())
            }

            application(Application::testApplication)

            runBlocking {
                val totalThreads = 100
                val deferred = (1..totalThreads).map {
                    async {
                        shouldThrow<RuntimeException> {
                            client.get("/exception")
                        }
                    }
                }

                val result = deferred.awaitAll().map { it.message }.groupBy { it }
                    .map { it.key to it.value.size }
                result.shouldBeSingleton {
                    it.second.shouldBe(totalThreads)
                }
            }
        }
    }

    @Test
    fun `check all parameters`() {
        val firstCacheKey = "/check?param1=value1&param2=value2"
        val secondCacheKey = "/check?param1=value1&param2=value2&param3=value3"
        val thirdKey = "/check?param1=value1&params=1,2"
        val cache = mutableMapOf<String, Any>()
        val provider = buildProvider(cache)

        testApplication {

            install(SimpleCache) {
                testCache(provider)
            }

            application(Application::testApplication)

            val firstResponse = client.get("/check?param2=value2&param1=value1")
            firstResponse.shouldHaveStatus(HttpStatusCode.OK)
            verify(provider, times(2)).getCache(eq(firstCacheKey))
            verify(provider, times(1)).setCache(eq(firstCacheKey), any(), anyOrNull())
            cache.keys.shouldBeSingleton {
                it.shouldBe(firstCacheKey)
            }

            val secondResponse = client.get("/check?param1=value1&param2=value2")
            secondResponse.shouldHaveStatus(HttpStatusCode.OK)
            secondResponse.bodyAsText().toInt().shouldBe(firstResponse.bodyAsText().toInt())
            verify(provider, times(3)).getCache(eq(firstCacheKey))
            verify(provider, times(1)).setCache(eq(firstCacheKey), any(), anyOrNull())
            cache.keys.shouldBeSingleton {
                it.shouldBe(firstCacheKey)
            }

            val thirdResponse = client.get("/check?param2=value2&param3=value3&param1=value1")
            thirdResponse.shouldHaveStatus(HttpStatusCode.OK)
            thirdResponse.bodyAsText().toInt().shouldNotBe(firstResponse.bodyAsText().toInt())
            verify(provider, times(2)).getCache(eq(secondCacheKey))
            verify(provider, times(1)).setCache(eq(secondCacheKey), any(), anyOrNull())
            cache.keys.shouldHaveSize(2).shouldContain(secondCacheKey)

            val fourthResponse = client.get("/check?params=1&params=2&param1=value1")
            fourthResponse.shouldHaveStatus(HttpStatusCode.OK)
            fourthResponse.bodyAsText().toInt().shouldNotBe(thirdResponse.bodyAsText().toInt())
            verify(provider, times(2)).getCache(eq(thirdKey))
            verify(provider, times(1)).setCache(eq(thirdKey), any(), anyOrNull())
            cache.keys.shouldHaveSize(3).shouldContain(thirdKey)

            val fifthResponse = client.get("/check?param1=value1&params=2&params=1")
            fifthResponse.shouldHaveStatus(HttpStatusCode.OK)
            fifthResponse.bodyAsText().toInt().shouldBe(fourthResponse.bodyAsText().toInt())
            verify(provider, times(3)).getCache(eq(thirdKey))
            verify(provider, times(1)).setCache(eq(thirdKey), any(), anyOrNull())
            cache.keys.shouldHaveSize(3).shouldContain(thirdKey)
        }
    }

    @Test
    fun `check parameters in keys`() {
        val cacheOneKey = "/check?param1=value1"
        val cacheTwoKeys = "/check?param1=value1&param3=value3"
        val cache = mutableMapOf<String, Any>()
        val provider = buildProvider(cache)

        testApplication {

            install(SimpleCache) {
                testCache(provider)
            }

            application(Application::testApplicationWithKeys)

            val firstResponse = client.get("/check?param2=value2&param1=value1")
            firstResponse.shouldHaveStatus(HttpStatusCode.OK)
            verify(provider, times(2)).getCache(eq(cacheOneKey))
            verify(provider, times(1)).setCache(eq(cacheOneKey), any(), anyOrNull())
            cache.keys.shouldBeSingleton {
                it.shouldBe(cacheOneKey)
            }

            val secondResponse = client.get("/check?param2=value21&param1=value1")
            secondResponse.shouldHaveStatus(HttpStatusCode.OK)
            secondResponse.bodyAsText().toInt().shouldBe(firstResponse.bodyAsText().toInt())
            verify(provider, times(3)).getCache(eq(cacheOneKey))
            verify(provider, times(1)).setCache(eq(cacheOneKey), any(), anyOrNull())
            cache.keys.shouldBeSingleton {
                it.shouldBe(cacheOneKey)
            }

            val thirdResponse = client.get("/check?param2=value2&param3=value3&param1=value1")
            thirdResponse.shouldHaveStatus(HttpStatusCode.OK)
            thirdResponse.bodyAsText().toInt().shouldNotBe(firstResponse.bodyAsText().toInt())
            verify(provider, times(2)).getCache(eq(cacheTwoKeys))
            verify(provider, times(1)).setCache(eq(cacheTwoKeys), any(), anyOrNull())
            cache.keys.shouldHaveSize(2).shouldContain(cacheTwoKeys)
        }
    }

    private fun buildProvider(cache: MutableMap<String, Any> = mutableMapOf(), invalidateDuration: Duration = 5.minutes): SimpleCacheProvider {
        val provider = mock<SimpleCacheProvider>(defaultAnswer = Answers.CALLS_REAL_METHODS) {
            on { invalidateAt } doReturn invalidateDuration
            onBlocking { setCache(anyString(), any(), anyOrNull()) } doAnswer {
                cache[it.arguments[0].toString()] = it.arguments[1]
            }
            onBlocking { getCache(anyString()) } doAnswer {
                cache[it.arguments[0]]
            }
        }

        provider::class.java.superclass.getDeclaredField("mutex").also { it.isAccessible = true }.set(provider, Mutex())

        return provider
    }
}