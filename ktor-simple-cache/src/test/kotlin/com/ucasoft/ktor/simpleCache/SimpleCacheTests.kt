package com.ucasoft.ktor.simpleCache

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.*
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

            val cache = TextContent("Test", ContentType.Any)

            val provider = mock<SimpleCacheProvider> {
                on { invalidateAt } doReturn 5.minutes
                on { getCache(anyString()) } doReturn cache
            }

            install(SimpleCache) {
                testCache(provider)
            }

            application(Application::testApplication)

            val response = client.get("/check").bodyAsText()

            response.shouldBe("Test")

            verify(provider, times(1)).getCache(eq("/check"))
            verify(provider, times(0)).setCache(anyString(), any(), anyOrNull())
        }
    }

    @Test
    fun `check cache work`() {
        testApplication {

            var cache: Any? = null

            val provider = mock<SimpleCacheProvider> {
                on { invalidateAt } doReturn 5.minutes
                on { setCache(anyString(), any(), anyOrNull()) } doAnswer {
                    cache = it.arguments[1]
                }
                on { getCache(anyString()) } doAnswer {
                    cache
                }
            }

            install(SimpleCache) {
                testCache(provider)
            }

            application(Application::testApplication)

            val response = client.get("/check").bodyAsText()

            response.shouldBe("Check response")

            verify(provider, times(1)).getCache(eq("/check"))
            verify(provider, times(1)).setCache(eq("/check"), any(), anyOrNull())
            cache.shouldNotBeNull()

            client.get("/check")

            verify(provider, times(2)).getCache(eq("/check"))
            verify(provider, times(1)).setCache(eq("/check"), any(), anyOrNull())
        }
    }

    @Test
    fun `check parameters in keys`() {
        testApplication {
            val cacheOneKey = "/check?param1=value1"
            val cacheTwoKeys = "/check?param1=value1&param3=value3"
            val cache = mutableMapOf<String, Any>()
            val provider = mock<SimpleCacheProvider> {
                on { invalidateAt } doReturn 5.minutes
                on { setCache(anyString(), any(), anyOrNull()) } doAnswer {
                    cache[it.arguments[0].toString()] = it.arguments[1]
                }
                on { getCache(anyString()) } doAnswer {
                    cache[it.arguments[0]]
                }
            }

            install(SimpleCache) {
                testCache(provider)
            }

            application(Application::testApplicationWithKeys)

            val firstResponse = client.get("/check?param2=value2&param1=value1")
            firstResponse.shouldHaveStatus(HttpStatusCode.OK)
            val firstData = firstResponse.bodyAsText().toInt()

            verify(provider, times(1)).getCache(eq(cacheOneKey))
            verify(provider, times(1)).setCache(eq(cacheOneKey), any(), anyOrNull())
            cache.keys.shouldBeSingleton {
                it.shouldBe(cacheOneKey)
            }

            val secondResponse = client.get("/check?param2=value21&param1=value1")
            secondResponse.shouldHaveStatus(HttpStatusCode.OK)
            val secondData = secondResponse.bodyAsText().toInt()

            secondData.shouldBe(firstData)
            verify(provider, times(2)).getCache(eq(cacheOneKey))
            verify(provider, times(1)).setCache(eq(cacheOneKey), any(), anyOrNull())
            cache.keys.shouldBeSingleton {
                it.shouldBe(cacheOneKey)
            }

            val thirdResponse = client.get("/check?param2=value2&param3=value3&param1=value1")
            thirdResponse.shouldHaveStatus(HttpStatusCode.OK)
            val thirdData = thirdResponse.bodyAsText().toInt()

            thirdData.shouldNotBe(firstData)
            verify(provider, times(1)).getCache(eq(cacheTwoKeys))
            verify(provider, times(1)).setCache(eq(cacheTwoKeys), any(), anyOrNull())
            cache.keys.shouldHaveSize(2).shouldContain(cacheTwoKeys)
        }
    }
}