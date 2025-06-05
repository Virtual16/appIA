package com.example.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class ClaudeProvider(private val prefs: KeyStore) : LLMProvider {

    private val api: Api

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${prefs.getKey("claude")}")
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.anthropic.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(Api::class.java)
    }

    override suspend fun sendMessage(messages: List<Message>): Message = withContext(Dispatchers.IO) {
        val request = ClaudeRequest(messages)
        val response = api.complete(request)
        Message("assistant", response.completion)
    }

    interface Api {
        @Headers(
            "Content-Type: application/json",
            "anthropic-version: 2023-06-01"
        )
        @POST("v1/complete")
        suspend fun complete(@Body request: ClaudeRequest): ClaudeResponse
    }
}

data class ClaudeRequest(val prompt: List<Message>, val model: String = "claude-instant-v1")
data class ClaudeResponse(val completion: String)
