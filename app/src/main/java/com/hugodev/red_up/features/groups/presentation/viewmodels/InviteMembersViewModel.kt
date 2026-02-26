package com.hugodev.red_up.features.groups.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.features.groups.domain.entities.User
import com.hugodev.red_up.features.groups.domain.usecases.InviteMemberUseCase
import com.hugodev.red_up.features.groups.domain.usecases.SearchUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InviteMembersUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null,
    val invitingUserId: Long? = null,
    val successMessage: String? = null
)

@HiltViewModel
class InviteMembersViewModel @Inject constructor(
    private val searchUsersUseCase: SearchUsersUseCase,
    private val inviteMemberUseCase: InviteMemberUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InviteMembersUiState())
    val uiState: StateFlow<InviteMembersUiState> = _uiState.asStateFlow()

    private var groupId: Long = 0

    fun setGroupId(id: Long) {
        groupId = id
    }

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(users = emptyList(), error = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            searchUsersUseCase(query)
                .onSuccess { users ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            users = users,
                            error = null
                        ) 
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            users = emptyList(),
                            error = error.message ?: "Error al buscar usuarios"
                        ) 
                    }
                }
        }
    }

    fun inviteMember(userId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(invitingUserId = userId) }

            inviteMemberUseCase(groupId, userId)
                .onSuccess { message ->
                    _uiState.update { 
                        it.copy(
                            invitingUserId = null,
                            successMessage = message,
                            users = it.users.filter { user -> user.id != userId }
                        ) 
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            invitingUserId = null,
                            error = error.message ?: "Error al invitar usuario"
                        ) 
                    }
                }
        }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
