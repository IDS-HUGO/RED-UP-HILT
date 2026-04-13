package com.hugodev.red_up.features.notifications.domain.usecases

import com.hugodev.red_up.features.notifications.domain.entities.Notification
import com.hugodev.red_up.features.notifications.domain.repositories.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(): Result<List<Notification>> {
        return repository.getNotifications()
    }
}

class MarkNotificationAsReadUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: Long): Result<Unit> {
        return repository.markAsRead(notificationId)
    }
}

class DeleteNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: Long): Result<Unit> {
        return repository.deleteNotification(notificationId)
    }
}

class ClearAllNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.clearAllNotifications()
    }
}

class GetNotificationSettingsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(): Result<NotificationSettings> {
        return repository.getNotificationSettings()
    }
}

class UpdateNotificationSettingsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(settings: NotificationSettings): Result<Unit> {
        return repository.updateNotificationSettings(settings)
    }
}