package com.ucasoft.ktor.simpleRedisCache

import com.redis.testcontainers.RedisContainer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

internal class RedisCacheTests {

    @Test
    fun `test redis cache`() {
        println(redisContainer.host)
        println(redisContainer.firstMappedPort)
    }

    companion object {

        @Container
        val redisContainer = RedisContainer(DockerImageName.parse("redis:latest"))

        @JvmStatic
        @BeforeAll
        fun setup() {
            redisContainer.start()
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            if (redisContainer.isRunning) {
                redisContainer.stop()
            }
        }
    }
}