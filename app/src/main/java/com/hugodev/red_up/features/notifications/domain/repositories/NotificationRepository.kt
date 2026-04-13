package com.hugodev.red_up.features.notifications.domain.repositories

import com.hugodev.red_up.features.notifications.domain.entities.Notification
import com.hugodev.red_up.features.notifications.domain.entities.NotificationSettings

interface NotificationRepository {
    suspend fun getNotifications(): Result<List<Notification>>
    suspend fun markAsRead(notificationId: Long): Result<Unit>
    suspend fun deleteNotification(notificationId: Long): Result<Unit>
    suspend fun clearAllNotifications(): Result<Unit>
    suspend fun getNotificationSettings(): Result<NotificationSettings>
    suspend fun updateNotificationSettings(settings: NotificationSettings): Result<Unit>
}