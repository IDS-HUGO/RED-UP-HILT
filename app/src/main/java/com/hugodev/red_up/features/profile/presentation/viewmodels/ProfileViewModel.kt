package com.hugodev.red_up.features.profile.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.core.network.ApiService
import com.hugodev.red_up.core.preferences.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

data class UserProfile(
    val id: Long,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val correoInstitucional: String,
    val fotoPerfil: String?,
    val biografia: String?,
    val telefono: String?,
    val carrera: String?,
    val cuatrimestre: Int?
)

data class UserStats(
    val totalSeguidores: Int,
    val totalSiguiendo: Int,
    val totalPublicaciones: Int,
    val totalComentarios: Int,
    val totalReacciones: Int
)

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null,
    val userStats: UserStats? = null,
    val yaSegue: Boolean = false,
    val esMismoUsuario: Boolean = false,
    val error: String? = null,
    val success: String? = null
)

class ProfileViewModel(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState

    fun loadCurrentUserProfile() {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            try {
                // TODO: Implementar llamada a API
                // val response = apiService.getMyProfile()
                // Simulación por ahora
                _profileState.value = _profileState.value.copy(
                    userProfile = UserProfile(
                        id = 1,
                        nombre = "Hugo",
                        apellidoPaterno = "Dev",
                        apellidoMaterno = "Online",
                        correoInstitucional = "hugo@Universidad.edu",
                        fotoPerfil = null,
                        biografia = "Desarrollador móvil",
                        telefono = "1234567890",
                        carrera = "Ingeniería Informática",
                        cuatrimestre = 5
                    ),
                    esMismoUsuario = true,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading profile", e)
                _profileState.value = _profileState.value.copy(
                    error = "Error al cargar perfil: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun loadUserProfile(userId: Long) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            try {
                // TODO: Implementar llamada a API para obtener perfil completo
                _profileState.value = _profileState.value.copy(isLoading = false)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading user profile", e)
                _profileState.value = _profileState.value.copy(
                    error = "Error al cargar perfil: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun loadUserStats(userId: Long) {
        viewModelScope.launch {
            try {
                // TODO: Implementar llamada a API
                _profileState.value = _profileState.value.copy(
                    userStats = UserStats(
                        totalSeguidores = 15,
                        totalSiguiendo = 20,
                        totalPublicaciones = 5,
                        totalComentarios = 12,
                        totalReacciones = 45
                    )
                )
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading stats", e)
                _profileState.value = _profileState.value.copy(
                    error = "Error al cargar estadísticas"
                )
            }
        }
    }

    fun followUser(userId: Long) {
        viewModelScope.launch {
            try {
                // TODO: Implementar llamada POST a /api/usuarios/{userId}/seguir
                _profileState.value = _profileState.value.copy(
                    yaSegue = true,
                    success = "Ahora sigues a este usuario"
                )
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error following user", e)
                _profileState.value = _profileState.value.copy(
                    error = "Error al seguir usuario"
                )
            }
        }
    }

    fun unfollowUser(userId: Long) {
        viewModelScope.launch {
            try {
                // TODO: Implementar llamada DELETE a /api/usuarios/{userId}/seguir
                _profileState.value = _profileState.value.copy(
                    yaSegue = false,
                    success = "Has dejado de seguir a este usuario"
                )
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error unfollowing user", e)
                _profileState.value = _profileState.value.copy(
                    error = "Error al dejar de seguir usuario"
                )
            }
        }
    }

    fun updateProfile(biography: String?, telefono: String?) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            try {
                // TODO: Implementar llamada PUT a /api/usuarios/perfil/actualizar
                val currentProfile = _profileState.value.userProfile
                if (currentProfile != null) {
                    _profileState.value = _profileState.value.copy(
                        userProfile = currentProfile.copy(
                            biografia = biography,
                            telefono = telefono
                        ),
                        success = "Perfil actualizado correctamente",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating profile", e)
                _profileState.value = _profileState.value.copy(
                    error = "Error al actualizar perfil: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun clearMessages() {
        _profileState.value = _profileState.value.copy(
            error = null,
            success = null
        )
    }
}
