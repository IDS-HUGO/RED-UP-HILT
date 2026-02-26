package com.hugodev.red_up.features.chat.domain.entities

data class ChatRoom(
    val id: String,
    val name: String,
    val type: String, // "directo" o "grupal"
    val lastMessage: String? = null,
    val lastMessageTime: String? = null,
    val unreadCount: Int = 0
)
