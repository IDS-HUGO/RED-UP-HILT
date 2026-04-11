package com.hugodev.red_up.features.chat.presentation.viewmodels

import android.util.Log
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.core.data.AuthPreferences
import com.hugodev.red_up.features.chat.data.repositories.SocketIoChatRepository
import com.hugodev.red_up.features.chat.domain.entities.ChatMessage
import com.hugodev.red_up.core.sync.SyncEventStore
import com.hugodev.red_up.features.chat.domain.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val newMessage: String = "",
    val isConnected: Boolean = false,
    val currentRoomId: String? = null,
    val currentRoomName: String? = null,
    val currentRoomType: String? = null,
    val currentUserId: String? = null,
    val error: String? = null,
    val pendingMessages: List<ChatMessage> = emptyList(),
    val isJoiningRoom: Boolean = false
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
    @ApplicationContext private val appContext: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    private val messageQueue = mutableListOf<ChatMessage>()

    val isConnected: StateFlow<Boolean> = observeChatConnectionUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        observeConnectionState()
        observeMessages()
        observeJoinedRoom()
        observeMessageHistory()
        initializeUserId()
    }

    private fun initializeUserId() {
        viewModelScope.launch {
            authPreferences.userIdFlow.collect { userId ->
                _uiState.update { it.copy(currentUserId = userId?.toString()) }
            }
        }
    }

    private fun observeConnectionState() {
        viewModelScope.launch {
            observeChatConnectionUseCase().collect { connected ->
                _uiState.update { it.copy(isConnected = connected) }
            }
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            observeChatMessagesUseCase().collect { message ->
                _uiState.update { state ->
                    if (state.currentRoomId != null && message.to == state.currentRoomId) {
                        state.copy(messages = state.messages + message)
                    } else state
                }
            }
        }
    }

    fun connectToChat() {
        viewModelScope.launch {
            val userId = authPreferences.userIdFlow.firstOrNull() ?: return@launch
            connectToChatUseCase(userId.toString())
        }
    }

    fun joinRoom(roomId: String, roomName: String, roomType: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    currentRoomName = roomName,
                    currentRoomType = roomType,
                    messages = emptyList(),
                    currentRoomId = null,
                    isJoiningRoom = true,
                    error = null
                )
            }
            try {
                if (roomType == "grupal") {
                    joinGroupChatUseCase(roomId)
                } else {
                    joinDirectChatUseCase(roomId)
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error joining room", e)
                _uiState.update { it.copy(error = "No se pudo unir al chat", isJoiningRoom = false) }
            }
        }
    }

    fun updateMessage(message: String) {
        _uiState.update { it.copy(newMessage = message) }
    }

    fun sendMessage() {
        val state = _uiState.value
        if (state.newMessage.isBlank() || state.currentUserId == null) return

        val timestamp = System.currentTimeMillis().toString()
        val message = ChatMessage(
            to = state.currentRoomId ?: "temp",
            message = state.newMessage,
            senderId = state.currentUserId,
            timestamp = timestamp,
            type = state.currentRoomType ?: "individual",
            messageType = "texto"
        )

        if (state.currentRoomId == null) {
            messageQueue.add(message)
            _uiState.update { it.copy(newMessage = "", pendingMessages = it.pendingMessages + message) }
        } else {
            sendChatMessageUseCase(message)
            viewModelScope.launch {
                SyncEventStore.queueEvent(
                    context = appContext,
                    eventType = "chat_message_sent",
                    payload = mapOf(
                        "room_id" to (state.currentRoomId ?: ""),
                        "room_type" to (state.currentRoomType ?: "individual"),
                        "timestamp" to System.currentTimeMillis()
                    )
                )
            }
            _uiState.update { it.copy(newMessage = "") }
        }
    }

    private fun observeJoinedRoom() {
        viewModelScope.launch {
            observeJoinedRoomUseCase().collect { salaUuid ->
                _uiState.update { it.copy(currentRoomId = salaUuid, isJoiningRoom = false, error = null) }
                socketIoChatRepository.loadMessageHistory(salaUuid)
                
                if (messageQueue.isNotEmpty()) {
                    messageQueue.forEach { sendChatMessageUseCase(it.copy(to = salaUuid)) }
                    messageQueue.clear()
                    _uiState.update { it.copy(pendingMessages = emptyList()) }
                }
            }
        }
    }

    private fun observeMessageHistory() {
        viewModelScope.launch {
            socketIoChatRepository.observeMessageHistory().collect { history ->
                _uiState.update { it.copy(messages = history) }
            }
        }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }

    override fun onCleared() {
        super.onCleared()
        disconnectFromChatUseCase()
    }
}
