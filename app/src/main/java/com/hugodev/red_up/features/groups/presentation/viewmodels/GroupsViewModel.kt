package com.hugodev.red_up.features.groups.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.features.groups.domain.entities.Group
import com.hugodev.red_up.features.groups.domain.entities.GroupDetail
import com.hugodev.red_up.features.groups.domain.usecases.CreateGroupUseCase
import com.hugodev.red_up.features.groups.domain.usecases.GetGroupDetailUseCase
import com.hugodev.red_up.features.groups.domain.usecases.GetMyGroupsUseCase
import com.hugodev.red_up.features.groups.domain.usecases.JoinGroupUseCase
import com.hugodev.red_up.features.groups.domain.usecases.SearchGroupsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupsListUiState(
    val groups: List<Group> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class GroupsListViewModel @Inject constructor(
    private val getMyGroupsUseCase: GetMyGroupsUseCase,
    private val searchGroupsUseCase: SearchGroupsUseCase,
    private val getGroupDetailUseCase: GetGroupDetailUseCase,
    private val joinGroupUseCase: JoinGroupUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupsListUiState())
    val uiState: StateFlow<GroupsListUiState> = _uiState.asStateFlow()

    init {
        loadMyGroups()
    }

    fun loadMyGroups() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getMyGroupsUseCase().fold(
                onSuccess = { groups ->
                    _uiState.value = _uiState.value.copy(
                        groups = groups,
                        isLoading = false
                    )
                },
                onFailure = { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = throwable.message ?: "Error al obtener grupos"
                    )
                }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isBlank()) {
            loadMyGroups()
        } else {
            searchGroups(query)
        }
    }

    private fun searchGroups(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            searchGroupsUseCase(query).fold(
                onSuccess = { groups ->
                    _uiState.value = _uiState.value.copy(
                        groups = groups,
                        isLoading = false
                    )
                },
                onFailure = { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = throwable.message ?: "Error al buscar grupos"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class CreateGroupUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val carreraId: Long = 1L,
    val privacidad: String = "publico",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val createdGroupId: Long? = null
)

@HiltViewModel
class CreateGroupViewModel @Inject constructor(
    private val createGroupUseCase: CreateGroupUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateGroupUiState())
    val uiState: StateFlow<CreateGroupUiState> = _uiState.asStateFlow()

    fun onNombreChange(nombre: String) {
        _uiState.value = _uiState.value.copy(nombre = nombre, error = null)
    }

    fun onDescripcionChange(descripcion: String) {
        _uiState.value = _uiState.value.copy(descripcion = descripcion, error = null)
    }

    fun onCarreraIdChange(carreraId: Long) {
        _uiState.value = _uiState.value.copy(carreraId = carreraId, error = null)
    }

    fun onPrivacidadChange(privacidad: String) {
        _uiState.value = _uiState.value.copy(privacidad = privacidad, error = null)
    }

    fun crearGrupo() {
        val state = _uiState.value
        if (state.nombre.isBlank()) {
            _uiState.value = state.copy(error = "El nombre es requerido")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            createGroupUseCase(
                nombre = state.nombre,
                descripcion = state.descripcion.takeIf { it.isNotBlank() },
                carreraId = state.carreraId,
                privacidad = state.privacidad
            ).fold(
                onSuccess = { group ->
                    _uiState.value = CreateGroupUiState(
                        isSuccess = true,
                        createdGroupId = group.id
                    )
                },
                onFailure = { throwable ->
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = throwable.message ?: "Error al crear grupo"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
