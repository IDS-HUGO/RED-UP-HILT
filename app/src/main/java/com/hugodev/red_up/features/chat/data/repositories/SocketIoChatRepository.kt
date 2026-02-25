package com.hugodev.red_up.features.chat.data.repositories

import com.hugodev.red_up.core.di.SocketBaseUrl
import com.hugodev.red_up.features.chat.domain.entities.ChatMessage
import com.hugodev.red_up.features.chat.domain.repositories.ChatRepository
import io.socket.client.IO
import io.socket.client.Socket
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject

@Singleton
class SocketIoChatRepository @Inject constructor(
    @SocketBaseUrl private val socketBaseUrl: String
) : ChatRepository {

    private val messagesFlow = MutableSharedFlow<ChatMessage>(extraBufferCapacity = 64)
    private val connectionFlow = MutableStateFlow(false)
    private var socket: Socket? = null

    override fun connect(userId: String) {
        if (socket?.connected() == true) return

        val options = IO.Options().apply {
            query = "user_id=$userId"
            transports = arrayOf("websocket")
            reconnection = true
        }

        socket = IO.socket(socketBaseUrl, options).apply {
            on(Socket.EVENT_CONNECT) {
                connectionFlow.value = true
            }
            on("connected") {
                connectionFlow.value = true
            }
            on(Socket.EVENT_DISCONNECT) {
                connectionFlow.value = false
            }
            on("receive_message") { args ->
                val payload = args.firstOrNull() as? JSONObject ?: return@on
                messagesFlow.tryEmit(payload.toChatMessage())
            }
            connect()
        }
    }

    override fun disconnect() {
        socket?.disconnect()
        socket?.off()
        socket = null
        connectionFlow.value = false
    }

    override fun joinGroup(groupId: String) {
        val payload = JSONObject().apply {
            put("group_id", groupId)
        }
        socket?.emit("join_group", payload)
    }

    override fun sendMessage(message: ChatMessage) {
        val payload = JSONObject().apply {
            put("to", message.to)
            put("message", message.message)
            put("sender_id", message.senderId)
            put("timestamp", message.timestamp)
            put("type", message.type)
        }
        socket?.emit("send_message", payload)
    }

    override fun observeMessages(): Flow<ChatMessage> = messagesFlow

    override fun observeConnection(): Flow<Boolean> = connectionFlow

    private fun JSONObject.toChatMessage(): ChatMessage {
        return ChatMessage(
            to = optString("to"),
            message = optString("message"),
            senderId = optString("sender_id"),
            timestamp = optString("timestamp"),
            type = optString("type")
        )
    }
}
