package com.hugodev.red_up.features.chat.data.datasources.remote.mapper

import com.hugodev.red_up.features.chat.data.datasources.remote.models.ChatMessageDto
import com.hugodev.red_up.features.chat.domain.entities.ChatMessage
import org.json.JSONObject

fun ChatMessageDto.toDomain(): ChatMessage {
    return ChatMessage(
        id = mensajeId,
        to = to,
        message = message,
        senderId = senderId,
        senderName = senderName,
        senderEmail = senderEmail,
        timestamp = timestamp,
        type = type,
        messageType = messageType,
        urlArchivo = urlArchivo
    )
}

fun ChatMessage.toDto(): ChatMessageDto {
    return ChatMessageDto(
        mensajeId = id,
        to = to,
        message = message,
        senderId = senderId,
        senderName = senderName,
        senderEmail = senderEmail,
        timestamp = timestamp,
        type = type,
        messageType = messageType,
        urlArchivo = urlArchivo
    )
}

fun JSONObject.toChatMessage(): ChatMessage {
    return ChatMessage(
        id = optString("mensaje_id", null),
        to = optString("sala_uuid").ifEmpty { optString("to") },  // Priorizar sala_uuid
        message = optString("message"),
        senderId = optString("from").ifEmpty { optString("sender_id") },  // Puede venir como 'from' o 'sender_id'
        senderName = optString("sender_name", null),
        senderEmail = optString("sender_email", null),
        timestamp = optString("timestamp"),
        type = optString("type", "directo"),
        messageType = optString("message_type", "texto"),
        urlArchivo = optString("url_archivo", null)
    )
}

fun ChatMessage.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("sala_uuid", to)  // 'to' contiene el sala_uuid
        put("message", message)
        put("sender_id", senderId)
        put("timestamp", timestamp)
        put("type", type)
        put("message_type", messageType)
        urlArchivo?.let { put("url_archivo", it) }
    }
}
