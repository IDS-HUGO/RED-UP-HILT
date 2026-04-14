package com.hugodev.red_up.features.auth.presentation.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.core.utils.compressImageForUpload
import com.hugodev.red_up.features.auth.domain.usecases.RegisterUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

data class RegisterUiState(
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val correo: String = "",
    val fechaNacimiento: String = "",
    val fotoUri: Uri? = null,
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private companion object {
        const val TAG = "RegisterImageUpload"
    }

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNombreChange(value: String) {
        _uiState.value = _uiState.value.copy(nombre = value, error = null)
    }

    fun onApellidoPaternoChange(value: String) {
        _uiState.value = _uiState.value.copy(apellidoPaterno = value, error = null)
    }

    fun onApellidoMaternoChange(value: String) {
        _uiState.value = _uiState.value.copy(apellidoMaterno = value, error = null)
    }

    fun onCorreoChange(value: String) {
        _uiState.value = _uiState.value.copy(correo = value, error = null)
    }

    fun onFechaNacimientoChange(value: String) {
        _uiState.value = _uiState.value.copy(fechaNacimiento = value, error = null)
    }

    fun onFotoUriChange(value: Uri?) {
        Log.d(TAG, "onFotoUriChange uri=$value")
        _uiState.value = _uiState.value.copy(fotoUri = value, error = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, error = null)
    }

    fun register() {
        val state = _uiState.value
        if (state.nombre.isBlank() || state.apellidoPaterno.isBlank()) {
            _uiState.value = state.copy(error = "Nombre y apellido son requeridos")
            return
        }
        if (state.correo.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "Correo y contrasena son requeridos")
            return
        }
        if (state.password != state.confirmPassword) {
            _uiState.value = state.copy(error = "Las contrasenas no coinciden")
            return
        }
        if (state.fechaNacimiento.isBlank()) {
            _uiState.value = state.copy(error = "Ingresa la fecha de nacimiento (YYYY-MM-DD)")
            return
        }

        val fechaNormalizada = normalizeFechaNacimiento(state.fechaNacimiento)
        if (fechaNormalizada == null) {
            _uiState.value = state.copy(error = "Fecha de nacimiento invalida. Usa YYYY-MM-DD")
            return
        }
        if (state.fotoUri != null && !isImageSizeValid(state.fotoUri)) {
            Log.w(TAG, "Image rejected by local size validation uri=${state.fotoUri}")
            _uiState.value = state.copy(error = "La imagen debe ser menor o igual a 5MB")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(
                isLoading = true,
                error = null,
                fechaNacimiento = fechaNormalizada
            )
            val fotoPart = state.fotoUri?.let { uri ->
                Log.d(TAG, "Creating multipart from uri=$uri")
                createMultipartBodyPart(uri)
            }
            if (state.fotoUri != null && fotoPart == null) {
                Log.e(TAG, "createMultipartBodyPart returned null uri=${state.fotoUri}")
                _uiState.value = state.copy(
                    isLoading = false,
                    error = "No se pudo procesar la imagen seleccionada"
                )
                return@launch
            }

            registerUseCase(
                email = state.correo.trim(),
                nombre = state.nombre.trim(),
                apellidoPaterno = state.apellidoPaterno.trim(),
                apellidoMaterno = state.apellidoMaterno.trim().takeIf { it.isNotEmpty() },
                fechaNacimiento = fechaNormalizada,
                fotoPerfil = fotoPart,
                password = state.password
            ).fold(
                onSuccess = {
                    Log.d(TAG, "Register success. Image sent=${fotoPart != null}")
                    _uiState.value = state.copy(isLoading = false, isSuccess = true)
                },
                onFailure = { throwable ->
                    Log.e(TAG, "Register failure with image=${fotoPart != null}", throwable)
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = throwable.message ?: "No se pudo registrar"
                    )
                }
            )
        }
    }

    private fun normalizeFechaNacimiento(input: String): String? {
        val trimmed = input.trim()
        val digits = trimmed.filter { it.isDigit() }
        val candidate = if (trimmed.contains("-")) {
            trimmed
        } else if (digits.length == 8) {
            "${digits.substring(0, 4)}-${digits.substring(4, 6)}-${digits.substring(6, 8)}"
        } else {
            return null
        }

        return try {
            LocalDate.parse(candidate, DateTimeFormatter.ISO_LOCAL_DATE)
            candidate
        } catch (ex: Exception) {
            null
        }
    }

    private fun isImageSizeValid(uri: Uri): Boolean {
        val size = context.contentResolver.openFileDescriptor(uri, "r")?.use { it.statSize } ?: return true
        val maxSizeBytes = 5L * 1024L * 1024L
        if (size <= 0L) return true
        Log.d(TAG, "isImageSizeValid uri=$uri size=$size max=$maxSizeBytes")
        return size <= maxSizeBytes
    }

    private fun createMultipartBodyPart(uri: Uri): MultipartBody.Part? {
        return try {
            val bytes = compressImageForUpload(context, uri) ?: return null
            Log.d(TAG, "Compressed image uri=$uri bytes=${bytes.size}")
            val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("foto_perfil", "profile_image.jpg", requestBody)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating multipart uri=$uri", e)
            null
        }
    }

}
