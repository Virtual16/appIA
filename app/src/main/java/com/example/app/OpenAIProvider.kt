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

class OpenAIProvider(
    private val prefs: KeyStore,
    baseUrl: String = "https://api.openai.com/"
) : LLMProvider {

    private val api: Api

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${prefs.getKey("openai")}")
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(Api::class.java)
    }

    override suspend fun sendMessage(messages: List<Message>): Message = withContext(Dispatchers.IO) {
        val request = ChatRequest(messages.map { ChatMessage(it.role, it.content) })
        val response = api.chat(request)
        Message("assistant", response.choices.first().message.content)
    }

    interface Api {
        @Headers("Content-Type: application/json")
        @POST("v1/chat/completions")
        suspend fun chat(@Body request: ChatRequest): ChatResponse
    }
}

data class ChatRequest(val messages: List<ChatMessage>, val model: String = "gpt-3.5-turbo")
data class ChatMessage(val role: String, val content: String)
data class ChatResponse(val choices: List<ChatChoice>)
data class ChatChoice(val message: ChatMessage)
