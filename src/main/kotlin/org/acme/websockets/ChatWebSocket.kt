package org.acme.websockets

import io.quarkus.websockets.next.*
import jakarta.inject.Inject

@WebSocket(path = "/chat/{username}")
class ChatWebSocket {
    // Declare the type of messages that can be sent and received
    enum class MessageType {
        USER_JOINED, USER_LEFT, CHAT_MESSAGE
    }

    @JvmRecord
    data class ChatMessage(
        val type: MessageType,
        val from: String,
        val message: String?
    )

    @Inject
    var connection: WebSocketConnection? = null

    @OnOpen(broadcast = true)
    fun onOpen(): ChatMessage {
        return ChatMessage(
            MessageType.USER_JOINED,
            connection!!.pathParam("username"),
            null
        )
    }

    @OnClose
    fun onClose() {
        val departure = ChatMessage(
                MessageType.USER_LEFT,
                connection!!.pathParam("username"),
                null
        )
        connection!!.broadcast()
            .sendTextAndAwait(departure)
    }

    @OnTextMessage(broadcast = true)
    fun onMessage(message: ChatMessage): ChatMessage {
        return message
    }
}