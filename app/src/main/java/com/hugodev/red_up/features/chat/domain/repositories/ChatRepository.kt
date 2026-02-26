package com.hugodev.red_up.features.chat.domain.repositories

import com.hugodev.red_up.features.chat.domain.entities.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun connect(userId: String)
    fun disconnect()
    fun joinGroup(groupId: String)
    fun sendMessage(message: ChatMessage)
    fun observeMessages(): Flow<ChatMessage>
    fun observeConnection(): Flow<Boolean>

    fun joinDirectChat(otherUserId: String)

    fun observeJoinedRoom(): Flow<String>

}
