package com.hugodev.red_up.features.chat.domain.entities

data class ChatMessage(
    val id: String? = null,
    val to: String,
    val message: String,
    val senderId: String,
    val senderName: String? = null,
    val timestamp: String,
    val type: String, // "directo" o "grupal"
    val messageType: String = "texto", // texto, imagen, archivo, audio, sistema
    val urlArchivo: String? = null
)
