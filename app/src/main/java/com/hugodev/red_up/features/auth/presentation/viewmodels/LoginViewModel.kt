package com.hugodev.red_up.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.hugodev.red_up.core.security.BiometricCredentialStore
import com.hugodev.red_up.core.sync.SyncEventStore
import com.hugodev.red_up.core.sync.SyncWork
import com.hugodev.red_up.features.auth.domain.usecases.ConfirmPasswordResetUseCase
import com.hugodev.red_up.features.auth.domain.usecases.LoginUseCase
import com.hugodev.red_up.features.auth.domain.usecases.RequestPasswordResetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val hasBiometricCredentials: Boolean = false,
    val isLoading: Boolean = false,
    val isForgotLoading: Boolean = false,
    val error: String? = null,
    val forgotMessage: String? = null,
    val forgotDebugCode: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val requestPasswordResetUseCase: RequestPasswordResetUseCase,
    private val confirmPasswordResetUseCase: ConfirmPasswordResetUseCase,
    private val biometricCredentialStore: BiometricCredentialStore,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LoginUiState(hasBiometricCredentials = biometricCredentialStore.hasCredentials())
    )
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, error = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun login() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "Completa tu correo y contrasena")
            return
        }

        loginInternal(
            email = state.email.trim(),
            password = state.password,
            saveForBiometric = true
        )
    }

    fun loginWithBiometric() {
        val credentials = biometricCredentialStore.getCredentials()
        if (credentials == null) {
            _uiState.value = _uiState.value.copy(
                error = "No hay credenciales guardadas para huella",
                hasBiometricCredentials = false
            )
            return
        }

        loginInternal(
            email = credentials.first,
            password = credentials.second,
            saveForBiometric = false
        )
    }

    private fun loginInternal(
        email: String,
        password: String,
        saveForBiometric: Boolean
    ) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            loginUseCase(email, password).fold(
                onSuccess = {
                    if (saveForBiometric) {
                        biometricCredentialStore.saveCredentials(email, password)
                    }
                    viewModelScope.launch {
                        SyncEventStore.queueEvent(
                            context = appContext,
                            eventType = "session_login_success",
                            payload = mapOf(
                                "login_mode" to if (saveForBiometric) "password" else "biometric",
                                "timestamp" to System.currentTimeMillis()
                            )
                        )
                    }
                    SyncWork.enqueueTokenSync(appContext)
                    _uiState.value = state.copy(
                        email = email,
                        password = password,
                        isLoading = false,
                        isSuccess = true,
                        hasBiometricCredentials = true
                    )
                },
                onFailure = { throwable ->
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = throwable.message ?: "No se pudo iniciar sesion"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun requestPasswordReset(email: String) {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Ingresa tu correo institucional")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isForgotLoading = true, error = null, forgotMessage = null)
            requestPasswordResetUseCase(email.trim()).fold(
                onSuccess = { debugCode ->
                    _uiState.value = _uiState.value.copy(
                        isForgotLoading = false,
                        forgotMessage = "Codigo enviado. Revisa el correo o usa el codigo de prueba.",
                        forgotDebugCode = debugCode
                    )
                },
                onFailure = { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isForgotLoading = false,
                        error = throwable.message ?: "No se pudo solicitar recuperacion"
                    )
                }
            )
        }
    }

    fun confirmPasswordReset(email: String, code: String, newPassword: String) {
        if (email.isBlank() || code.isBlank() || newPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Completa correo, codigo y nueva contrasena")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isForgotLoading = true, error = null, forgotMessage = null)
            confirmPasswordResetUseCase(email.trim(), code.trim(), newPassword).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isForgotLoading = false,
                        forgotMessage = "Contrasena actualizada. Ya puedes iniciar sesion.",
                        forgotDebugCode = null
                    )
                },
                onFailure = { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isForgotLoading = false,
                        error = throwable.message ?: "No se pudo actualizar la contrasena"
                    )
                }
            )
        }
    }

    fun clearForgotState() {
        _uiState.value = _uiState.value.copy(forgotMessage = null, forgotDebugCode = null, error = null)
    }
}
