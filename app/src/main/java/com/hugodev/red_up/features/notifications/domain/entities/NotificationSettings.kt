package com.hugodev.red_up.features.notifications.domain.entities

data class NotificationSettings(
    val likes: Boolean = true,
    val comments: Boolean = true,
    val follows: Boolean = true,
    val messages: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)