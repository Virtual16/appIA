package com.example.app

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals

class OpenAIProviderTest {
    private lateinit var server: MockWebServer
    private lateinit var provider: OpenAIProvider

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        val keyStore = object : KeyStore(null) {
            override fun getKey(name: String): String? = "testkey"
        }
        provider = OpenAIProvider(keyStore, server.url("/").toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun testSendMessage() = runBlocking {
        val body = "{""choices"": [{""message"": {""content"": ""hello""}}]}"
        server.enqueue(MockResponse().setBody(body).setResponseCode(200))

        val result = provider.sendMessage(listOf(Message("user", "hi")))

        assertEquals("assistant", result.role)
        assertEquals("hello", result.content)
    }
}
