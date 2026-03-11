package com.hugodev.red_up.features.individual_chat.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.features.individual_chat.presentation.screens.ChatUser
import com.hugodev.red_up.features.groups.domain.entities.User
import com.hugodev.red_up.features.groups.domain.usecases.SearchUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IndividualChatUiState(
    val chatUsuarios: List<ChatUser> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedUserId: String? = null
)

@HiltViewModel
class IndividualChatViewModel @Inject constructor(
    private val searchUsersUseCase: SearchUsersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(IndividualChatUiState())
    val uiState: StateFlow<IndividualChatUiState> = _uiState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadChats()
    }

    private fun loadChats() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        // TODO: Implementar lógica de carga de chats individuales
        // - Obtener lista de conversaciones del usuario
        // - Ordenar por timestamp del último mensaje
        // - Mostrar indicator de en línea
        _uiState.value = _uiState.value.copy(isLoading = false)
    }

    fun searchUsers(query: String) {
        searchJob?.cancel()
        
        if (query.length < 3) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        searchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(300) // Debounce
            
            searchUsersUseCase(query).fold(
                onSuccess = { users ->
                    _searchResults.value = users
                    _isSearching.value = false
                },
                onFailure = { error ->
                    _searchResults.value = emptyList()
                    _isSearching.value = false
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Error al buscar usuarios"
                    )
                }
            )
        }
    }

    fun selectChat(userId: String) {
        _uiState.value = _uiState.value.copy(selectedUserId = userId)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
