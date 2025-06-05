package com.example.app

import org.junit.Test
import org.junit.Assert.assertEquals

class MessageFlowTest {
    @Test
    fun testMessageAdapter() {
        val adapter = MessageAdapter()
        val message = Message("user", "hi")
        adapter.addMessage(message)
        assertEquals(1, adapter.itemCount)
    }
}
