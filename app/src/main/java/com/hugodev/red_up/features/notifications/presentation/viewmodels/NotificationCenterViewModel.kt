package com.hugodev.red_up.features.notifications.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.features.publications.data.datasources.remote.api.UpRedApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationCenterUiState(
    val notifications: List<NotificationItem> = emptyList(),
    val isLoading: Boolean = false
)

data class NotificationItem(
    val id: Long,
    val title: String,
    val message: String,
    val timestamp: String
)

@HiltViewModel
class NotificationCenterViewModel @Inject constructor(
    private val upRedApi: UpRedApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationCenterUiState())
    val uiState: StateFlow<NotificationCenterUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val notifications = upRedApi.getNotifications().map { notification ->
                    NotificationItem(
                        id = notification.id,
                        title = notification.titulo,
                        message = notification.cuerpo ?: notification.tipo,
                        timestamp = notification.creadaEn ?: ""
                    )
                }
                _uiState.value = _uiState.value.copy(notifications = notifications, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}