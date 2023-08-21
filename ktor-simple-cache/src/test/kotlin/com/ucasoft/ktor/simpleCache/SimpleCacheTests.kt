package com.ucasoft.ktor.simpleCache

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
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

            verify(provider, times(1)).getCache("/check")
            verify(provider, times(0)).setCache(anyString(), any(), anyOrNull())
        }
    }
}