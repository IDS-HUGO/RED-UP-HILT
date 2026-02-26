package com.hugodev.red_up.features.groups_chat.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.features.groups_chat.presentation.screens.GroupChatItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class GroupsUiState(
    val grupos: List<GroupChatItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedGrupoId: String? = null
)

@HiltViewModel
class GroupsChatViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState: StateFlow<GroupsUiState> = _uiState.asStateFlow()

    init {
        loadGroups()
    }

    private fun loadGroups() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        // TODO: Implementar lógica de carga de grupos
        // - Obtener lista de grupos del usuario
        // - Ordenar por fecha de último mensaje
        // - Mostrar contador de miembros
    }

    fun createGroup(nombre: String, descripcion: String) {
        // TODO: Implementar creación de grupo
        // - Validar parámetros
        // - Llamar a API POST /api/grupos
        // - Agregar nuevo grupo a la lista
    }

    fun selectGroup(grupoId: String) {
        _uiState.value = _uiState.value.copy(selectedGrupoId = grupoId)
    }

    fun deleteGroup(grupoId: String) {
        // TODO: Implementar eliminación de grupo
        // - Llamar a API DELETE /api/grupos/{id}
        // - Remover de la lista
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
