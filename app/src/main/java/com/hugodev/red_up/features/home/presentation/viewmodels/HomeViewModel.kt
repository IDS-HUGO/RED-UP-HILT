package com.hugodev.red_up.features.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.core.data.AuthPreferences
import com.hugodev.red_up.features.chat.domain.entities.ChatMessage
import com.hugodev.red_up.features.chat.domain.repositories.ChatRepository
import com.hugodev.red_up.features.publications.domain.entities.Publications
import com.hugodev.red_up.features.publications.domain.usecases.GetPublicationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val publicaciones: List<Publications> = emptyList(),
    val mensajes: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isSocketConnected: Boolean = false,
    val error: String? = null,
    val userId: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPublicationUseCase: GetPublicationUseCase,
    private val chatRepository: ChatRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeUser()
        observeSocket()
    }

    fun loadFeed() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getPublicationUseCase().fold(
                onSuccess = { publicaciones ->
                    _uiState.update { it.copy(publicaciones = publicaciones, isLoading = false) }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "No se pudo cargar el feed"
                        )
                    }
                }
            )
        }
    }

    fun connectSocket() {
        val id = _uiState.value.userId ?: return
        chatRepository.connect(id)
    }

    fun disconnectSocket() {
        chatRepository.disconnect()
    }

    fun sendMessage(to: String, message: String) {
        val senderId = _uiState.value.userId ?: return
        if (to.isBlank() || message.isBlank()) return
        chatRepository.sendMessage(
            ChatMessage(
                to = to,
                message = message,
                senderId = senderId,
                timestamp = java.time.Instant.now().toString(),
                type = "text"
            )
        )
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun observeUser() {
        viewModelScope.launch {
            authPreferences.userIdFlow.collect { id ->
                _uiState.update { it.copy(userId = id?.toString()) }
            }
        }
    }

    private fun observeSocket() {
        viewModelScope.launch {
            chatRepository.observeConnection().collect { connected ->
                _uiState.update { it.copy(isSocketConnected = connected) }
            }
        }

        viewModelScope.launch {
            chatRepository.observeMessages().collect { message ->
                _uiState.update { current ->
                    current.copy(mensajes = listOf(message) + current.mensajes.take(9))
                }
            }
        }
    }
}
