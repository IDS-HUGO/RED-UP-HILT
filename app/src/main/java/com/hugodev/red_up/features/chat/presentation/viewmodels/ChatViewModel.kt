package com.hugodev.red_up.features.chat.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.core.data.AuthPreferences
import com.hugodev.red_up.features.chat.data.repositories.SocketIoChatRepository
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
    val error: String? = null,
    val pendingMessages: List<ChatMessage> = emptyList()
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
    private val socketIoChatRepository: SocketIoChatRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    private val messageQueue = mutableListOf<ChatMessage>()

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
        observeMessageHistory()
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
                val currentRoomId = _uiState.value.currentRoomId
                if (currentRoomId.isNullOrBlank() || message.to != currentRoomId) return@collect
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

        // Usar when en lugar de if-if independientes para evitar que se ejecuten ambos
        when (roomType) {
            "grupal" -> {
                joinGroupChatUseCase(roomId)

                _uiState.value = _uiState.value.copy(
                    currentRoomName = roomName,
                    currentRoomType = roomType,
                    messages = emptyList()
                )
                // ⚠️ NO seteamos currentRoomId aquí para chat grupal
                // Se setea cuando llegue group_joined con el sala_uuid correcto
            }
            "directo" -> {
                joinDirectChatUseCase(roomId)

                _uiState.value = _uiState.value.copy(
                    currentRoomName = roomName,
                    currentRoomType = roomType,
                    messages = emptyList()
                )
                // ⚠️ NO seteamos currentRoomId aquí
                // Se setea cuando llegue direct_chat_joined
            }
            else -> {
                _uiState.value = _uiState.value.copy(
                    error = "Tipo de sala desconocido: $roomType"
                )
            }
        }
    }

    fun updateMessage(message: String) {
        _uiState.value = _uiState.value.copy(newMessage = message)
    }

    fun sendMessage() {
        val state = _uiState.value
        
        if (state.newMessage.isBlank()) return
        if (state.currentUserId.isNullOrBlank()) {
            _uiState.value = state.copy(error = "Usuario no identificado")
            return
        }
        if (state.currentRoomType.isNullOrBlank()) {
            _uiState.value = state.copy(error = "Tipo de sala no definido")
            return
        }

        val message = ChatMessage(
            to = state.currentRoomId ?: "temp",
            message = state.newMessage,
            senderId = state.currentUserId,
            timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT),
            type = state.currentRoomType,
            messageType = "texto"
        )

        // Si no hay sala activa aún, encolar el mensaje
        if (state.currentRoomId.isNullOrBlank()) {
            messageQueue.add(message)
            val updatedPending = _uiState.value.pendingMessages + message
            _uiState.value = state.copy(
                newMessage = "",
                pendingMessages = updatedPending
            )
        } else {
            // Enviar inmediatamente si hay sala activa
            sendChatMessageUseCase(message)
            _uiState.value = state.copy(newMessage = "")
        }
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
                
                // Cargar historial de mensajes
                socketIoChatRepository.loadMessageHistory(salaUuid, limit = 50)
                
                // Enviar mensajes que estaban pendientes
                if (messageQueue.isNotEmpty()) {
                    messageQueue.forEach { message ->
                        val updatedMessage = message.copy(to = salaUuid)
                        sendChatMessageUseCase(updatedMessage)
                    }
                    messageQueue.clear()
                    _uiState.value = _uiState.value.copy(pendingMessages = emptyList())
                }
            }
        }
    }

    private fun observeMessageHistory() {
        viewModelScope.launch {
            socketIoChatRepository.observeMessageHistory().collect { historyMessages ->
                val currentRoomId = _uiState.value.currentRoomId
                val historyRoomId = historyMessages.firstOrNull()?.to
                if (currentRoomId.isNullOrBlank() || historyRoomId.isNullOrBlank()) return@collect
                if (historyRoomId != currentRoomId) return@collect

                val mergedMessages = mergeHistory(
                    current = _uiState.value.messages,
                    history = historyMessages
                )
                _uiState.value = _uiState.value.copy(messages = mergedMessages)
            }
        }
    }

    private fun mergeHistory(
        current: List<ChatMessage>,
        history: List<ChatMessage>
    ): List<ChatMessage> {
        val seen = HashSet<String>(history.size + current.size)
        val merged = ArrayList<ChatMessage>(history.size + current.size)

        for (message in history) {
            val key = messageKey(message)
            if (seen.add(key)) merged.add(message)
        }

        for (message in current) {
            val key = messageKey(message)
            if (seen.add(key)) merged.add(message)
        }

        return merged
    }

    private fun messageKey(message: ChatMessage): String {
        return message.id ?: "${message.senderId}|${message.timestamp}|${message.message}"
    }
}
