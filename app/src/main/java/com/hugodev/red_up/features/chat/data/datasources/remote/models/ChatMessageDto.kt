package com.hugodev.red_up.features.chat.data.datasources.remote.models

import com.google.gson.annotations.SerializedName

data class ChatMessageDto(
    @SerializedName("mensaje_id") val mensajeId: String? = null,
    @SerializedName("to") val to: String,
    @SerializedName("message") val message: String,
    @SerializedName("sender_id") val senderId: String,
    @SerializedName("sender_name") val senderName: String? = null,
    @SerializedName("sender_email") val senderEmail: String? = null,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("type") val type: String,
    @SerializedName("message_type") val messageType: String = "texto",
    @SerializedName("url_archivo") val urlArchivo: String? = null
)
