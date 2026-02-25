package com.hugodev.red_up.features.chat.domain.entities

data class ChatMessage(
    val to: String,
    val message: String,
    val senderId: String,
    val timestamp: String,
    val type: String
)
