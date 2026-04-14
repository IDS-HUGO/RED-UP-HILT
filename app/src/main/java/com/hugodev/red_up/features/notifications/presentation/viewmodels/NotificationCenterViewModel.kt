package com.hugodev.red_up.features.notifications.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.core.data.AuthPreferences
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
    val timestamp: Long
)

@HiltViewModel
class NotificationCenterViewModel @Inject constructor(
    private val upRedApi: UpRedApi,
    private val authPreferences: AuthPreferences
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
                // Simular carga de notificaciones desde API
                val notifications = listOf(
                    NotificationItem(1, "Nuevo mensaje", "Tienes un mensaje nuevo", System.currentTimeMillis()),
                    NotificationItem(2, "Like en publicación", "Tu publicación recibió un like", System.currentTimeMillis() - 3600000)
                )
                _uiState.value = _uiState.value.copy(notifications = notifications, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}