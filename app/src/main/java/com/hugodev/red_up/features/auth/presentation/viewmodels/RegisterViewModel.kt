package com.hugodev.red_up.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.features.auth.domain.usecases.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val correo: String = "",
    val fechaNacimiento: String = "",
    val fotoUrl: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

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

    fun onFotoUrlChange(value: String) {
        _uiState.value = _uiState.value.copy(fotoUrl = value, error = null)
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

        viewModelScope.launch {
            _uiState.value = state.copy(
                isLoading = true,
                error = null,
                fechaNacimiento = fechaNormalizada
            )
            registerUseCase(
                email = state.correo.trim(),
                nombre = state.nombre.trim(),
                apellidoPaterno = state.apellidoPaterno.trim(),
                apellidoMaterno = state.apellidoMaterno.trim().takeIf { it.isNotEmpty() },
                fechaNacimiento = fechaNormalizada,
                fotoUrl = state.fotoUrl.trim().takeIf { it.isNotEmpty() },
                password = state.password
            ).fold(
                onSuccess = {
                    _uiState.value = state.copy(isLoading = false, isSuccess = true)
                },
                onFailure = { throwable ->
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
