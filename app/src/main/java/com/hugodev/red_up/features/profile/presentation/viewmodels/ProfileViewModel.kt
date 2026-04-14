package com.hugodev.red_up.features.profile.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.features.publications.data.datasources.remote.api.UpRedApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

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
                Log.e("ProfileViewModel", "Error loading current profile", e)
                _profileState.value = _profileState.value.copy(
                    error = "Error al cargar tu perfil: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun loadUserProfile(userId: Long) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true, error = null, userProfile = null, userStats = null)
            try {
                // Cargamos siempre stats (lo más probable que exista)
                val stats = upRedApi.getUserStats(userId)

                // Intentamos cargar el perfil completo, pero si falla,
                // construimos un perfil mínimo a partir de las stats.
                val profile = try {
                    upRedApi.getUserProfile(userId)
                } catch (e: Exception) {
                    null
                }

                val userProfile = profile?.let {
                    UserProfile(
                        id = it.id,
                        nombre = it.nombre,
                        apellidoPaterno = it.apellidoPaterno,
                        apellidoMaterno = it.apellidoMaterno.orEmpty(),
                        correoInstitucional = it.correoInstitucional,
                        fotoPerfil = it.fotoPerfilUrl,
                        biografia = it.biografia,
                        telefono = it.telefono,
                        carrera = it.carrera?.nombre,
                        cuatrimestre = it.cuatrimestre?.numero
                    )
                } ?: UserProfile(
                    id = userId,
                    nombre = "Usuario $userId",
                    apellidoPaterno = "",
                    apellidoMaterno = "",
                    correoInstitucional = "",
                    fotoPerfil = null,
                    biografia = null,
                    telefono = null,
                    carrera = null,
                    cuatrimestre = null
                )

                _profileState.value = _profileState.value.copy(
                    userProfile = userProfile,
                    userStats = UserStats(
                        totalSeguidores = stats.totalSeguidores,
                        totalSiguiendo = stats.totalSiguiendo,
                        totalPublicaciones = stats.totalPublicaciones,
                        totalComentarios = stats.totalComentarios,
                        totalReacciones = stats.totalReacciones
                    ),
                    esMismoUsuario = false,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading user profile", e)
                _profileState.value = _profileState.value.copy(
                    error = "Error al cargar el perfil escaneado",
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
            }
        }
    }

    fun followUser(userId: Long) {
        viewModelScope.launch {
            try {
                val response = upRedApi.followUser(userId)
                _profileState.value = _profileState.value.copy(yaSegue = true, success = response.message)
                loadUserStats(userId)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error following user", e)
            }
        }
    }

    fun unfollowUser(userId: Long) {
        viewModelScope.launch {
            try {
                val response = upRedApi.unfollowUser(userId)
                _profileState.value = _profileState.value.copy(yaSegue = false, success = response.message)
                loadUserStats(userId)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error unfollowing user", e)
            }
        }
    }

    fun updateProfile(biography: String?, telefono: String?, fotoPart: MultipartBody.Part?) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            try {
                val updated = upRedApi.updateCurrentProfile(
                    biografia = biography?.toRequestBody("text/plain".toMediaType()),
                    telefono = telefono?.toRequestBody("text/plain".toMediaType()),
                    fotoPerfil = fotoPart
                )
                _profileState.value = _profileState.value.copy(
                    userProfile = _profileState.value.userProfile?.copy(
                        nombre = updated.nombre,
                        apellidoPaterno = updated.apellidoPaterno,
                        biografia = updated.biografia,
                        telefono = updated.telefono,
                        fotoPerfil = updated.fotoPerfilUrl
                    ),
                    success = "Perfil actualizado",
                    isLoading = false
                )
            } catch (e: Exception) {
                _profileState.value = _profileState.value.copy(error = "Error al actualizar", isLoading = false)
            }
        }
    }

    fun clearMessages() {
        _profileState.value = _profileState.value.copy(error = null, success = null)
    }
}
