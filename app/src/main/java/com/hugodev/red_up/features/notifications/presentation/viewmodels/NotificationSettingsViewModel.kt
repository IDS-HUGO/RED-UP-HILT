package com.hugodev.red_up.features.notifications.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.features.notifications.domain.usecases.GetNotificationSettingsUseCase
import com.hugodev.red_up.features.notifications.domain.usecases.UpdateNotificationSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationSettings(
    val likes: Boolean = true,
    val comments: Boolean = true,
    val follows: Boolean = true,
    val messages: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)

data class NotificationSettingsUiState(
    val settings: NotificationSettings = NotificationSettings(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetNotificationSettingsUseCase,
    private val updateSettingsUseCase: UpdateNotificationSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getSettingsUseCase().fold(
                onSuccess = { settings ->
                    _uiState.update { it.copy(settings = settings, isLoading = false) }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Error al cargar configuración"
                        )
                    }
                }
            )
        }
    }

    fun updateSetting(key: String, value: Boolean) {
        val currentSettings = _uiState.value.settings
        val updatedSettings = when (key) {
            "likes" -> currentSettings.copy(likes = value)
            "comments" -> currentSettings.copy(comments = value)
            "follows" -> currentSettings.copy(follows = value)
            "messages" -> currentSettings.copy(messages = value)
            "sound" -> currentSettings.copy(soundEnabled = value)
            "vibration" -> currentSettings.copy(vibrationEnabled = value)
            else -> currentSettings
        }
        _uiState.update { it.copy(settings = updatedSettings) }
        saveSettings(updatedSettings)
    }

    private fun saveSettings(settings: NotificationSettings) {
        viewModelScope.launch {
            updateSettingsUseCase(settings).fold(
                onSuccess = { /* Success */ },
                onFailure = { throwable ->
                    _uiState.update { it.copy(error = throwable.message ?: "Error al guardar") }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}