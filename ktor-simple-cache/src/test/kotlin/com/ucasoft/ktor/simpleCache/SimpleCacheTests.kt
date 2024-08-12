package com.ucasoft.ktor.simpleCache

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
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
        val provider = buildProvider(mutableMapOf("/check" to TextContent("Test", ContentType.Any)))
        runTest(provider, Application::testApplication, listOf("/check")) { _, responses ->
            responses.shouldBeSingleton {
                it.content.shouldBe("Test")
            }

            verify(provider, times(1)).getCache(eq("/check"))
            verify(provider, never()).setCache(anyString(), any(), anyOrNull())
        }
    }

    @Test
    fun `check bad response not cached`() {
        val cache = mutableMapOf<String, Any>()
        val provider = buildProvider(cache)

        runTest(provider, Application::testApplication, listOf("/bad", "/bad")) { iteration, responses ->
            when(iteration) {
                0 -> {
                    responses.shouldBeSingleton {
                        it.status().shouldBe(HttpStatusCode.BadRequest)
                    }
                    verify(provider, times(2)).getCache(eq("/bad"))
                    verify(provider, times(1)).badResponse()
                    verify(provider, times(0)).setCache(eq("/bad"), any(), anyOrNull())
                    cache.shouldBeEmpty()
                }
                1 -> {
                    responses.shouldHaveSize(2)
                    verify(provider, times(4)).getCache(eq("/bad"))
                    verify(provider, times(2)).badResponse()
                    verify(provider, times(0)).setCache(eq("/bad"), any(), anyOrNull())
                    cache.shouldBeEmpty()
                }
            }
        }
    }

    @Test
    fun `check cache work`() {

        val cache = mutableMapOf<String, Any>()
        val provider = buildProvider(cache)

        runTest(provider, Application::testApplication, listOf("/check", "/check")) { iteration, responses ->
            when (iteration) {
                0 -> {
                    responses.shouldBeSingleton {
                        it.content?.toIntOrNull().shouldNotBeNull()
                    }
                    verify(provider, times(2)).getCache(eq("/check"))
                    verify(provider, times(1)).setCache(eq("/check"), any(), anyOrNull())
                    cache.shouldNotBeEmpty()
                }
                1 -> {
                    verify(provider, times(3)).getCache(eq("/check"))
                    verify(provider, times(1)).setCache(eq("/check"), any(), anyOrNull())
                }
            }
        }
    }

    @Test
    fun `check cache is concurrency`() {
        val provider = buildProvider()
        with(buildTestEngine(provider, Application::testApplication)) {

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
        with(buildTestEngine(buildProvider(), Application::testApplication)) {

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
    fun `check all parameters`() {
        val firstCacheKey = "/check?param1=value1&param2=value2"
        val secondCacheKey = "/check?param1=value1&param2=value2&param3=value3"
        val thirdKey = "/check?param1=value1&params=1,2"
        val cache = mutableMapOf<String, Any>()
        val provider = buildProvider(cache)

        runTest(
            provider,
            Application::testApplication,
            listOf(
                "/check?param2=value2&param1=value1",
                "/check?param1=value1&param2=value2",
                "/check?param2=value2&param3=value3&param1=value1",
                "/check?params=1&params=2&param1=value1",
                "/check?param1=value1&params=2&params=1"
            )
        ) { iteration, responses ->
            when(iteration) {
                0 -> {
                    responses[iteration].shouldHaveStatus(HttpStatusCode.OK)
                    verify(provider, times(2)).getCache(eq(firstCacheKey))
                    verify(provider, times(1)).setCache(eq(firstCacheKey), any(), anyOrNull())
                    cache.keys.shouldBeSingleton {
                        it.shouldBe(firstCacheKey)
                    }
                }
                1 -> {
                    responses[iteration].shouldHaveStatus(HttpStatusCode.OK)
                    responses[iteration].content?.toInt().shouldBe(responses[0].content?.toInt())
                    verify(provider, times(3)).getCache(eq(firstCacheKey))
                    verify(provider, times(1)).setCache(eq(firstCacheKey), any(), anyOrNull())
                    cache.keys.shouldBeSingleton {
                        it.shouldBe(firstCacheKey)
                    }
                }
                2 -> {
                    responses[iteration].shouldHaveStatus(HttpStatusCode.OK)
                    responses[iteration].content?.toInt().shouldNotBe(responses[0].content?.toInt())
                    verify(provider, times(2)).getCache(eq(secondCacheKey))
                    verify(provider, times(1)).setCache(eq(secondCacheKey), any(), anyOrNull())
                    cache.keys.shouldHaveSize(2).shouldContain(secondCacheKey)
                }
                3 -> {
                    responses[iteration].shouldHaveStatus(HttpStatusCode.OK)
                    responses[iteration].content?.toInt().shouldNotBe(responses[2].content?.toInt())
                    verify(provider, times(2)).getCache(eq(thirdKey))
                    verify(provider, times(1)).setCache(eq(thirdKey), any(), anyOrNull())
                    cache.keys.shouldHaveSize(3).shouldContain(thirdKey)
                }
                4 -> {
                    responses[iteration].shouldHaveStatus(HttpStatusCode.OK)
                    responses[iteration].content?.toInt().shouldBe(responses[3].content?.toInt())
                    verify(provider, times(3)).getCache(eq(thirdKey))
                    verify(provider, times(1)).setCache(eq(thirdKey), any(), anyOrNull())
                    cache.keys.shouldHaveSize(3).shouldContain(thirdKey)
                }
            }
        }
    }

    @Test
    fun `check parameters in keys`() {
        val cacheOneKey = "/check?param1=value1"
        val cacheTwoKeys = "/check?param1=value1&param3=value3"
        val cache = mutableMapOf<String, Any>()
        val provider = buildProvider(cache)

        runTest(
            provider,
            Application::testApplicationWithKeys,
            listOf(
                "/check?param2=value2&param1=value1",
                "/check?param2=value21&param1=value1",
                "/check?param2=value2&param3=value3&param1=value1"
            )
        ) { iteration, responses ->
            when(iteration) {
                0 -> {
                    responses[iteration].shouldHaveStatus(HttpStatusCode.OK)
                    verify(provider, times(2)).getCache(eq(cacheOneKey))
                    verify(provider, times(1)).setCache(eq(cacheOneKey), any(), anyOrNull())
                    cache.keys.shouldBeSingleton {
                        it.shouldBe(cacheOneKey)
                    }
                }
                1 -> {
                    responses[iteration].shouldHaveStatus(HttpStatusCode.OK)
                    responses[iteration].content?.toInt().shouldBe(responses[0].content?.toInt())
                    verify(provider, times(3)).getCache(eq(cacheOneKey))
                    verify(provider, times(1)).setCache(eq(cacheOneKey), any(), anyOrNull())
                    cache.keys.shouldBeSingleton {
                        it.shouldBe(cacheOneKey)
                    }
                }
                2 -> {
                    responses[iteration].shouldHaveStatus(HttpStatusCode.OK)
                    responses[iteration].content?.toInt().shouldNotBe(responses[0].content?.toInt())
                    verify(provider, times(2)).getCache(eq(cacheTwoKeys))
                    verify(provider, times(1)).setCache(eq(cacheTwoKeys), any(), anyOrNull())
                    cache.keys.shouldHaveSize(2).shouldContain(cacheTwoKeys)
                }
            }
        }
    }

    @Test
    fun `check only get is cached`(){
        val provider = buildProvider()
        with(buildTestEngine(provider, Application::testApplication)) {
            runBlocking {
                val response = client.post("/post")
                response.shouldHaveStatus(HttpStatusCode.OK)
                verify(provider, never()).setCache(eq("/post"), any(), anyOrNull())
                provider.getCache("/post").shouldBeNull()
            }
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

    private fun buildTestEngine(provider: SimpleCacheProvider, applicationInit: (Application) -> Unit): TestApplicationEngine {
        val engine = TestApplicationEngine(createTestEnvironment())
        engine.start(wait = true)
        with(engine) {
            application.install(SimpleCache) {
                testCache(provider)
            }
            applicationInit.invoke(application)
        }

        return engine
    }

    private fun runTest(provider: SimpleCacheProvider,
                        applicationInit: (Application) -> Unit,
                        urls: List<String>,
                        doAssertions: suspend (iteration: Int, responses: List<TestApplicationResponse>) -> Unit) {
        val responses = mutableListOf<TestApplicationResponse>()
        with(buildTestEngine(provider, applicationInit)) {
            urls.forEachIndexed { index, url ->
                responses.add(handleRequest(HttpMethod.Get, url).response)
                runBlocking {
                    doAssertions(index, responses)
                }
            }
        }
    }
}