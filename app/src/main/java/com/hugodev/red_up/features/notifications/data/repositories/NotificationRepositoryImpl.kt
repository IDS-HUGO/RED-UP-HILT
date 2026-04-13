package com.hugodev.red_up.features.notifications.data.repositories

import com.hugodev.red_up.features.notifications.domain.entities.Notification
import com.hugodev.red_up.features.notifications.domain.entities.NotificationSettings
import com.hugodev.red_up.features.notifications.domain.repositories.NotificationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor() : NotificationRepository {

    // Dummy implementation - replace with actual API calls
    private val dummyNotifications = listOf(
        Notification(
            id = 1,
            title = "Nuevo comentario",
            message = "Juan comentó en tu publicación",
            type = "comment",
            isRead = false,
            createdAt = "2024-01-15 10:30",
            actionUrl = "comments/123",
            senderId = 456,
            senderName = "Juan Pérez"
        ),
        Notification(
            id = 2,
            title = "Mensaje recibido",
            message = "Tienes un nuevo mensaje en el chat",
            type = "chat",
            isRead = true,
            createdAt = "2024-01-14 15:45",
            actionUrl = "chat/room123/Chat Grupo/individual",
            senderId = 789,
            senderName = "María García"
        )
    )

    private var settings = NotificationSettings()

    override suspend fun getNotifications(): Result<List<Notification>> {
        // Simulate API call
        return Result.success(dummyNotifications)
    }

    override suspend fun markAsRead(notificationId: Long): Result<Unit> {
        // Simulate API call
        return Result.success(Unit)
    }

    override suspend fun deleteNotification(notificationId: Long): Result<Unit> {
        // Simulate API call
        return Result.success(Unit)
    }

    override suspend fun clearAllNotifications(): Result<Unit> {
        // Simulate API call
        return Result.success(Unit)
    }

    override suspend fun getNotificationSettings(): Result<NotificationSettings> {
        return Result.success(settings)
    }

    override suspend fun updateNotificationSettings(settings: NotificationSettings): Result<Unit> {
        this.settings = settings
        return Result.success(Unit)
    }
}