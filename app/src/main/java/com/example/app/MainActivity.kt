package com.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var provider: LLMProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val messageList = findViewById<RecyclerView>(R.id.messageList)
        val inputField = findViewById<EditText>(R.id.inputField)

        val keyStore = KeyStore(this)
        provider = OpenAIProvider(keyStore)
        messageAdapter = MessageAdapter()
        messageList.adapter = messageAdapter
        messageList.layoutManager = LinearLayoutManager(this)

        inputField.setOnEditorActionListener { v, _, _ ->
            val text = inputField.text.toString()
            if (text.isNotBlank()) {
                val userMessage = Message(role = "user", content = text)
                messageAdapter.addMessage(userMessage)
                inputField.text.clear()
                lifecycleScope.launch {
                    val reply = provider.sendMessage(listOf(userMessage))
                    messageAdapter.addMessage(reply)
                }
            }
            true
        }
    }
}
