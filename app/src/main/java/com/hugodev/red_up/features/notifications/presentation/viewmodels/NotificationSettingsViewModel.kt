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

data class NotificationSettingsUiState(
    val pushEnabled: Boolean = true,
    val chatEnabled: Boolean = true,
    val groupsEnabled: Boolean = true,
    val socialEnabled: Boolean = true
)

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val upRedApi: UpRedApi,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val config = upRedApi.getNotificationConfig()
                _uiState.value = NotificationSettingsUiState(
                    pushEnabled = config.pushEnabled,
                    chatEnabled = config.chatEnabled,
                    groupsEnabled = config.groupsEnabled,
                    socialEnabled = config.socialEnabled
                )
            } catch (e: Exception) {
                // Usar valores por defecto
            }
        }
    }

    fun updatePushEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(pushEnabled = enabled)
        saveSettings()
    }

    fun updateChatEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(chatEnabled = enabled)
        saveSettings()
    }

    fun updateGroupsEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(groupsEnabled = enabled)
        saveSettings()
    }

    fun updateSocialEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(socialEnabled = enabled)
        saveSettings()
    }

    private fun saveSettings() {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                upRedApi.updateNotificationConfig(
                    com.hugodev.red_up.features.publications.data.datasources.remote.models.NotificationConfigDto(
                        pushEnabled = state.pushEnabled,
                        chatEnabled = state.chatEnabled,
                        groupsEnabled = state.groupsEnabled,
                        socialEnabled = state.socialEnabled
                    )
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}