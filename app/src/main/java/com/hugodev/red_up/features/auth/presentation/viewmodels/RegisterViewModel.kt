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
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val nombreError: String? = null,
    val apellidoPaternoError: String? = null,
    val correoError: String? = null,
    val fechaNacimientoError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNombreChange(value: String) {
        _uiState.value = _uiState.value.copy(nombre = value, error = null, nombreError = null)
    }

    fun onApellidoPaternoChange(value: String) {
        _uiState.value = _uiState.value.copy(apellidoPaterno = value, error = null, apellidoPaternoError = null)
    }

    fun onApellidoMaternoChange(value: String) {
        _uiState.value = _uiState.value.copy(apellidoMaterno = value, error = null)
    }

    fun onCorreoChange(value: String) {
        _uiState.value = _uiState.value.copy(correo = value, error = null, correoError = null)
    }

    fun onFechaNacimientoChange(value: String) {
        _uiState.value = _uiState.value.copy(fechaNacimiento = value, error = null, fechaNacimientoError = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null, passwordError = null, confirmPasswordError = null)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, error = null, confirmPasswordError = null)
    }

    private fun validateFields(): Boolean {
        val state = _uiState.value
        var isValid = true
        var nombreError: String? = null
        var apellidoPaternoError: String? = null
        var correoError: String? = null
        var fechaNacimientoError: String? = null
        var passwordError: String? = null
        var confirmPasswordError: String? = null

        if (state.nombre.isBlank()) {
            nombreError = "El nombre es obligatorio"
            isValid = false
        }

        if (state.apellidoPaterno.isBlank()) {
            apellidoPaternoError = "El apellido paterno es obligatorio"
            isValid = false
        }

        if (state.correo.isBlank()) {
            correoError = "El correo electrónico es obligatorio"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.correo).matches()) {
            correoError = "Ingresa un correo electrónico válido"
            isValid = false
        }

        if (state.fechaNacimiento.isBlank()) {
            fechaNacimientoError = "La fecha de nacimiento es obligatoria"
            isValid = false
        } else if (normalizeFechaNacimiento(state.fechaNacimiento) == null) {
            fechaNacimientoError = "Fecha inválida. Usa el formato YYYY-MM-DD"
            isValid = false
        }

        if (state.password.isBlank()) {
            passwordError = "La contraseña es obligatoria"
            isValid = false
        } else if (state.password.length < 8) {
            passwordError = "La contraseña debe tener al menos 8 caracteres"
            isValid = false
        } else if (!state.password.any { it.isUpperCase() }) {
            passwordError = "La contraseña debe contener al menos una letra mayúscula"
            isValid = false
        } else if (!state.password.any { it.isLowerCase() }) {
            passwordError = "La contraseña debe contener al menos una letra minúscula"
            isValid = false
        } else if (!state.password.any { it.isDigit() }) {
            passwordError = "La contraseña debe contener al menos un número"
            isValid = false
        }

        if (state.confirmPassword.isBlank()) {
            confirmPasswordError = "Confirma tu contraseña"
            isValid = false
        } else if (state.password != state.confirmPassword) {
            confirmPasswordError = "Las contraseñas no coinciden"
            isValid = false
        }

        _uiState.value = state.copy(
            nombreError = nombreError,
            apellidoPaternoError = apellidoPaternoError,
            correoError = correoError,
            fechaNacimientoError = fechaNacimientoError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError
        )
        return isValid
    }

    fun register() {
        if (!validateFields()) {
            return
        }

        val state = _uiState.value
        val fechaNormalizada = normalizeFechaNacimiento(state.fechaNacimiento)!!

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
