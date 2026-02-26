package com.hugodev.red_up.features.chat.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.core.data.AuthPreferences
import com.hugodev.red_up.features.chat.domain.entities.ChatMessage
import com.hugodev.red_up.features.chat.domain.usecases.ConnectToChatUseCase
import com.hugodev.red_up.features.chat.domain.usecases.DisconnectFromChatUseCase
import com.hugodev.red_up.features.chat.domain.usecases.JoinGroupChatUseCase
import com.hugodev.red_up.features.chat.domain.usecases.ObserveChatConnectionUseCase
import com.hugodev.red_up.features.chat.domain.usecases.ObserveChatMessagesUseCase
import com.hugodev.red_up.features.chat.domain.usecases.SendChatMessageUseCase
import com.hugodev.red_up.features.chat.domain.usecases.JoinDirectChatUseCase
import com.hugodev.red_up.features.chat.domain.usecases.ObserveJoinedRoomUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val newMessage: String = "",
    val isConnected: Boolean = false,
    val currentRoomId: String? = null,
    val currentRoomName: String? = null,
    val currentRoomType: String? = null, // "directo" o "grupal"
    val currentUserId: String? = null,
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val connectToChatUseCase: ConnectToChatUseCase,
    private val disconnectFromChatUseCase: DisconnectFromChatUseCase,
    private val joinGroupChatUseCase: JoinGroupChatUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val observeChatMessagesUseCase: ObserveChatMessagesUseCase,
    private val observeChatConnectionUseCase: ObserveChatConnectionUseCase,
    private val authPreferences: AuthPreferences,

    private val joinDirectChatUseCase: JoinDirectChatUseCase,
    private val observeJoinedRoomUseCase: ObserveJoinedRoomUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    val isConnected: StateFlow<Boolean> = observeChatConnectionUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        observeMessages()
        observeConnectionState()
        observeJoinedRoom()
        initializeUserId()
    }

    private fun initializeUserId() {
        viewModelScope.launch {
            authPreferences.userIdFlow.collect { userId ->
                _uiState.value = _uiState.value.copy(currentUserId = userId?.toString())
            }
        }
    }

    private fun observeConnectionState() {
        viewModelScope.launch {
            observeChatConnectionUseCase().collect { connected ->
                _uiState.value = _uiState.value.copy(isConnected = connected)
            }
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            observeChatMessagesUseCase().collect { message ->
                val currentMessages = _uiState.value.messages.toMutableList()
                currentMessages.add(message)
                _uiState.value = _uiState.value.copy(messages = currentMessages)
            }
        }
    }

    fun connectToChat() {
        viewModelScope.launch {
            val userId = authPreferences.userIdFlow.firstOrNull()
            if (userId == null) {
                _uiState.value = _uiState.value.copy(error = "Usuario no autenticado")
                return@launch
            }
            connectToChatUseCase(userId.toString())
        }
    }

    fun joinRoom(roomId: String, roomName: String, roomType: String) {

        if (!_uiState.value.isConnected) {
            _uiState.value = _uiState.value.copy(error = "No conectado al servidor")
            return
        }

        if (roomType == "grupal") {
            joinGroupChatUseCase(roomId)

            _uiState.value = _uiState.value.copy(
                currentRoomName = roomName,
                currentRoomType = roomType,
                messages = emptyList()
            )
            // ⚠️ NO seteamos currentRoomId aquí para chat grupal
            // Se setea cuando llegue group_joined con el sala_uuid correcto
        }

        if (roomType == "directo") {
            joinDirectChatUseCase(roomId)

            _uiState.value = _uiState.value.copy(
                currentRoomName = roomName,
                currentRoomType = roomType,
                messages = emptyList()
            )
            // ⚠️ NO seteamos currentRoomId aquí
            // Se setea cuando llegue direct_chat_joined
        }
    }

    fun updateMessage(message: String) {
        _uiState.value = _uiState.value.copy(newMessage = message)
    }

    fun sendMessage() {
        val state = _uiState.value
        
        if (state.newMessage.isBlank()) return
        if (state.currentRoomId.isNullOrBlank()) {
            _uiState.value = state.copy(error = "No hay sala activa")
            return
        }
        if (state.currentUserId.isNullOrBlank()) {
            _uiState.value = state.copy(error = "Usuario no identificado")
            return
        }
        if (state.currentRoomType.isNullOrBlank()) {
            _uiState.value = state.copy(error = "Tipo de sala no definido")
            return
        }

        val message = ChatMessage(
            to = state.currentRoomId,
            message = state.newMessage,
            senderId = state.currentUserId,
            timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT),
            type = state.currentRoomType,
            messageType = "texto"
        )

        sendChatMessageUseCase(message)
        _uiState.value = state.copy(newMessage = "")
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        disconnectFromChatUseCase()
    }

    private fun observeJoinedRoom() {
        viewModelScope.launch {
            observeJoinedRoomUseCase().collect { salaUuid ->
                _uiState.value = _uiState.value.copy(
                    currentRoomId = salaUuid
                )
            }
        }
    }
}
