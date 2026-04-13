package com.hugodev.red_up.features.notifications.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.features.notifications.domain.entities.Notification
import com.hugodev.red_up.features.notifications.domain.usecases.GetNotificationsUseCase
import com.hugodev.red_up.features.notifications.domain.usecases.MarkNotificationAsReadUseCase
import com.hugodev.red_up.features.notifications.domain.usecases.DeleteNotificationUseCase
import com.hugodev.red_up.features.notifications.domain.usecases.ClearAllNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationCenterUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NotificationCenterViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markAsReadUseCase: MarkNotificationAsReadUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase,
    private val clearAllUseCase: ClearAllNotificationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationCenterUiState())
    val uiState: StateFlow<NotificationCenterUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getNotificationsUseCase().fold(
                onSuccess = { notifications ->
                    _uiState.update { it.copy(notifications = notifications, isLoading = false) }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Error al cargar notificaciones"
                        )
                    }
                }
            )
        }
    }

    fun markAsRead(notificationId: Long) {
        viewModelScope.launch {
            markAsReadUseCase(notificationId).fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(
                            notifications = state.notifications.map {
                                if (it.id == notificationId) it.copy(isRead = true) else it
                            }
                        )
                    }
                },
                onFailure = { /* Handle error if needed */ }
            )
        }
    }

    fun deleteNotification(notificationId: Long) {
        viewModelScope.launch {
            deleteNotificationUseCase(notificationId).fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(notifications = state.notifications.filter { it.id != notificationId })
                    }
                },
                onFailure = { /* Handle error if needed */ }
            )
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            clearAllUseCase().fold(
                onSuccess = {
                    _uiState.update { it.copy(notifications = emptyList()) }
                },
                onFailure = { /* Handle error if needed */ }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}