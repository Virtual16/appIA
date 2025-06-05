package com.example.app

interface LLMProvider {
    suspend fun sendMessage(messages: List<Message>): Message
}
