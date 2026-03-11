package com.hugodev.red_up.features.groups_chat.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.features.groups_chat.presentation.screens.GroupChatItem
import com.hugodev.red_up.features.groups.domain.usecases.GetMyGroupsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupsUiState(
    val grupos: List<GroupChatItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedGrupoId: String? = null
)

@HiltViewModel
class GroupsChatViewModel @Inject constructor(
    private val getMyGroupsUseCase: GetMyGroupsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState: StateFlow<GroupsUiState> = _uiState.asStateFlow()

    init {
        loadMyGroups()
    }

    fun loadMyGroups() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getMyGroupsUseCase().fold(
                onSuccess = { groups ->
                    val groupChatItems = groups.map {
                        GroupChatItem(
                            id = it.id.toString(),
                            nombre = it.nombre,
                            descripcion = it.descripcion,
                            totalMiembros = it.totalMiembros
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        grupos = groupChatItems,
                        isLoading = false
                    )
                },
                onFailure = { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = throwable.message ?: "Error al cargar grupos"
                    )
                }
            )
        }
    }

    fun selectGroup(grupoId: String) {
        _uiState.value = _uiState.value.copy(selectedGrupoId = grupoId)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
