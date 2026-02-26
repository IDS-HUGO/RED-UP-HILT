package com.hugodev.red_up.features.individual_chat.presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hugodev.red_up.features.individual_chat.presentation.screens.ChatUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class IndividualChatUiState(
    val chatUsuarios: List<ChatUser> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedUserId: String? = null
)

@HiltViewModel
class IndividualChatViewModel @Inject constructor() : ViewModel() {

    var uiState = mutableStateOf(IndividualChatUiState())
        private set

    init {
        loadChats()
    }

    private fun loadChats() {
        uiState.value = uiState.value.copy(isLoading = true)
        // TODO: Implementar lógica de carga de chats individuales
        // - Obtener lista de conversaciones del usuario
        // - Ordenar por timestamp del último mensaje
        // - Mostrar indicator de en línea
    }

    fun searchUserByEmail(email: String) {
        // TODO: Implementar búsqueda de usuario por correo
        // - Llamar a API o WebSocket search_user_by_email
        // - Crear nueva conversación si no existe
    }

    fun selectChat(userId: String) {
        uiState.value = uiState.value.copy(selectedUserId = userId)
    }

    fun clearError() {
        uiState.value = uiState.value.copy(error = null)
    }
}
