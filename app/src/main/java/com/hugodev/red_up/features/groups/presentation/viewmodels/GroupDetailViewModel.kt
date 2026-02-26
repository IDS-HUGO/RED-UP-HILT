package com.hugodev.red_up.features.groups.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.core.data.AuthPreferences
import com.hugodev.red_up.features.groups.domain.entities.GroupDetail
import com.hugodev.red_up.features.groups.domain.usecases.GetGroupDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupDetailUiState(
    val isLoading: Boolean = false,
    val group: GroupDetail? = null,
    val error: String? = null,
    val canInviteMembers: Boolean = false
)

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    private val getGroupDetailUseCase: GetGroupDetailUseCase,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupDetailUiState())
    val uiState: StateFlow<GroupDetailUiState> = _uiState.asStateFlow()

    fun loadGroupDetail(groupId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val currentUserId = authPreferences.userIdFlow.firstOrNull()

            getGroupDetailUseCase(groupId)
                .onSuccess { groupDetail ->
                    // Check if current user can invite members (is admin or owner)
                    val canInvite = if (currentUserId != null && groupDetail.miembros.isNotEmpty()) {
                        groupDetail.miembros.any { member ->
                            member.usuarioId == currentUserId &&
                            member.rolMiembro in listOf("dueno", "admin") &&
                            member.estadoMembresia == "activo"
                        }
                    } else {
                        false
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            group = groupDetail,
                            canInviteMembers = canInvite,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al cargar detalles del grupo"
                        )
                    }
                }
        }
    }
}
