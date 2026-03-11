package com.hugodev.red_up.features.profile.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.features.publications.data.datasources.remote.api.UpRedApi
import com.hugodev.red_up.features.publications.data.datasources.remote.models.UpdateProfileRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val upRedApi: UpRedApi
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState

    fun loadCurrentUserProfile() {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true, error = null)
            try {
                val profile = upRedApi.getCurrentProfile()
                val stats = upRedApi.getUserStats(profile.id)

                _profileState.value = _profileState.value.copy(
                    userProfile = UserProfile(
                        id = profile.id,
                        nombre = profile.nombre,
                        apellidoPaterno = profile.apellidoPaterno,
                        apellidoMaterno = profile.apellidoMaterno.orEmpty(),
                        correoInstitucional = profile.correoInstitucional,
                        fotoPerfil = profile.fotoPerfilUrl,
                        biografia = profile.biografia,
                        telefono = profile.telefono,
                        carrera = profile.carrera?.nombre,
                        cuatrimestre = profile.cuatrimestre?.numero
                    ),
                    userStats = UserStats(
                        totalSeguidores = stats.totalSeguidores,
                        totalSiguiendo = stats.totalSiguiendo,
                        totalPublicaciones = stats.totalPublicaciones,
                        totalComentarios = stats.totalComentarios,
                        totalReacciones = stats.totalReacciones
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
            _profileState.value = _profileState.value.copy(isLoading = true, error = null)
            try {
                val stats = upRedApi.getUserStats(userId)
                _profileState.value = _profileState.value.copy(
                    userStats = UserStats(
                        totalSeguidores = stats.totalSeguidores,
                        totalSiguiendo = stats.totalSiguiendo,
                        totalPublicaciones = stats.totalPublicaciones,
                        totalComentarios = stats.totalComentarios,
                        totalReacciones = stats.totalReacciones
                    ),
                    isLoading = false
                )
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
                val stats = upRedApi.getUserStats(userId)
                _profileState.value = _profileState.value.copy(
                    userStats = UserStats(
                        totalSeguidores = stats.totalSeguidores,
                        totalSiguiendo = stats.totalSiguiendo,
                        totalPublicaciones = stats.totalPublicaciones,
                        totalComentarios = stats.totalComentarios,
                        totalReacciones = stats.totalReacciones
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
                val response = upRedApi.followUser(userId)
                _profileState.value = _profileState.value.copy(
                    yaSegue = true,
                    success = response.message
                )
                loadUserStats(userId)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error following user", e)
                _profileState.value = _profileState.value.copy(
                    error = "Error al seguir usuario: ${e.message}"
                )
            }
        }
    }

    fun unfollowUser(userId: Long) {
        viewModelScope.launch {
            try {
                val response = upRedApi.unfollowUser(userId)
                _profileState.value = _profileState.value.copy(
                    yaSegue = false,
                    success = response.message
                )
                loadUserStats(userId)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error unfollowing user", e)
                _profileState.value = _profileState.value.copy(
                    error = "Error al dejar de seguir usuario: ${e.message}"
                )
            }
        }
    }

    fun updateProfile(biography: String?, telefono: String?) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            try {
                val updated = upRedApi.updateCurrentProfile(
                    UpdateProfileRequestDto(
                        biografia = biography,
                        telefono = telefono
                    )
                )
                _profileState.value = _profileState.value.copy(
                    userProfile = _profileState.value.userProfile?.copy(
                        nombre = updated.nombre,
                        apellidoPaterno = updated.apellidoPaterno,
                        apellidoMaterno = updated.apellidoMaterno.orEmpty(),
                        correoInstitucional = updated.correoInstitucional,
                        biografia = updated.biografia,
                        telefono = updated.telefono,
                        carrera = updated.carrera?.nombre,
                        cuatrimestre = updated.cuatrimestre?.numero,
                        fotoPerfil = updated.fotoPerfilUrl
                    ),
                    success = "Perfil actualizado correctamente",
                    isLoading = false
                )
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
