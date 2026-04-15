package com.hugodev.red_up.features.notifications.presentation.viewmodels

import android.util.Log
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
    val isLoading: Boolean = false,
    val errorMessage: String? = null
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

    fun refresh() {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val notifications = upRedApi.getNotifications().map { notification ->
                    NotificationItem(
                        id = notification.id,
                        title = notification.titulo,
                        message = notification.cuerpo ?: notification.tipo,
                        timestamp = notification.creadaEn ?: ""
                    )
                }
                _uiState.value = _uiState.value.copy(
                    notifications = notifications,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                Log.e("NotificationCenter", "Error cargando notificaciones", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "No se pudieron cargar las notificaciones"
                )
            }
        }
    }
}