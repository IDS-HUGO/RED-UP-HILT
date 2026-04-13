package com.hugodev.red_up.features.notifications.domain.entities

data class Notification(
    val id: Long,
    val title: String,
    val message: String,
    val type: String, // e.g., "like", "comment", "follow", "chat"
    val isRead: Boolean = false,
    val createdAt: String,
    val actionUrl: String? = null, // For deep linking
    val senderId: Long? = null,
    val senderName: String? = null
)