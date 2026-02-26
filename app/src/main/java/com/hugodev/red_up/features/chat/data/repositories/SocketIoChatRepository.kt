package com.hugodev.red_up.features.chat.data.repositories

import android.util.Log
import com.hugodev.red_up.core.di.SocketBaseUrl
import com.hugodev.red_up.features.chat.data.datasources.remote.mapper.toChatMessage
import com.hugodev.red_up.features.chat.data.datasources.remote.mapper.toJsonObject
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
    private val TAG = "SocketIoChatRepository"

    private val roomFlow = MutableSharedFlow<String>(replay = 1)

    override fun connect(userId: String) {
        if (socket?.connected() == true) {
            Log.d(TAG, "Already connected")
            return
        }

        try {
            val options = IO.Options().apply {
                query = "user_id=$userId"
                transports = arrayOf("websocket")
                reconnection = true
                reconnectionDelay = 1000
                reconnectionDelayMax = 5000
            }

            socket = IO.socket(socketBaseUrl, options).apply {
                on(Socket.EVENT_CONNECT) {
                    Log.d(TAG, "WebSocket connected")
                    connectionFlow.value = true
                }
                
                on("connected") { args ->
                    Log.d(TAG, "Server acknowledged connection: ${args.contentToString()}")
                    connectionFlow.value = true
                }
                
                on(Socket.EVENT_DISCONNECT) {
                    Log.d(TAG, "WebSocket disconnected")
                    connectionFlow.value = false
                }
                
                on(Socket.EVENT_CONNECT_ERROR) { args ->
                    Log.e(TAG, "Connection error: ${args.contentToString()}")
                    connectionFlow.value = false
                }
                
                on("receive_message") { args ->
                    try {
                        val payload = args.firstOrNull() as? JSONObject ?: return@on
                        Log.d(TAG, "Received message: $payload")
                        val message = payload.toChatMessage()
                        messagesFlow.tryEmit(message)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing message", e)
                    }
                }
                
                on("ack") { args ->
                    Log.d(TAG, "Message acknowledged: ${args.contentToString()}")
                }

                on("direct_chat_joined") { args ->
                    try {
                        val payload = args.firstOrNull() as? JSONObject ?: return@on
                        val salaUuid = payload.optString("sala_uuid")
                        Log.d(TAG, "Direct chat joined with sala_uuid=$salaUuid")
                        roomFlow.tryEmit(salaUuid)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing direct_chat_joined", e)
                    }
                }
                
                connect()
            }
            
            Log.d(TAG, "Connecting to $socketBaseUrl with user_id=$userId")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating socket", e)
        }
    }

    override fun disconnect() {
        Log.d(TAG, "Disconnecting...")
        socket?.disconnect()
        socket?.off()
        socket = null
        connectionFlow.value = false
    }

    override fun joinGroup(groupId: String) {
        val payload = JSONObject().apply {
            put("group_id", groupId)
        }
        Log.d(TAG, "Joining group: $groupId")
        socket?.emit("join_group", payload)
    }

    override fun sendMessage(message: ChatMessage) {
        val payload = message.toJsonObject()
        Log.d(TAG, "Sending message: $payload")
        socket?.emit("send_message", payload)
    }

    override fun observeMessages(): Flow<ChatMessage> = messagesFlow

    override fun observeConnection(): Flow<Boolean> = connectionFlow

    override fun joinDirectChat(otherUserId: String) {

        val payload = JSONObject().apply {
            put("other_user_id", otherUserId)
        }

        if (socket?.connected() == true) {
            Log.d(TAG, "Joining direct chat with user $otherUserId")
            socket?.emit("join_direct_chat", payload)
        } else {
            Log.e(TAG, "Cannot join direct chat: Socket not connected")
        }
    }

    override fun observeJoinedRoom(): Flow<String> = roomFlow
}
