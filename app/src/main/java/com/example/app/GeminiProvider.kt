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

class GeminiProvider(private val prefs: KeyStore) : LLMProvider {

    private val api: Api

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${prefs.getKey("gemini")}")
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(Api::class.java)
    }

    override suspend fun sendMessage(messages: List<Message>): Message = withContext(Dispatchers.IO) {
        val request = GeminiRequest(messages.joinToString("\n") { it.content })
        val response = api.generate(request)
        Message("assistant", response.candidates.first().output)
    }

    interface Api {
        @Headers("Content-Type: application/json")
        @POST("v1beta/models/gemini-pro:generateText")
        suspend fun generate(@Body request: GeminiRequest): GeminiResponse
    }
}

data class GeminiRequest(val prompt: String)
data class GeminiResponse(val candidates: List<GeminiCandidate>)
data class GeminiCandidate(val output: String)
