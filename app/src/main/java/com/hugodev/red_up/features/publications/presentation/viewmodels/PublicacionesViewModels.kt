package com.hugodev.red_up.features.publications.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.features.publications.domain.entities.Publications
import com.hugodev.red_up.features.publications.domain.usecases.CreatePublicationUseCase
import com.hugodev.red_up.features.publications.domain.usecases.GetPublicationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PublicacionesListUiState(
    val publicaciones: List<Publications> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PublicacionesListViewModel @Inject constructor(
    private val getPublicationUseCase: GetPublicationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicacionesListUiState())
    val uiState: StateFlow<PublicacionesListUiState> = _uiState.asStateFlow()

    fun loadPublicaciones() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getPublicationUseCase().fold(
                onSuccess = { publications ->
                    _uiState.value = _uiState.value.copy(
                        publicaciones = publications,
                        isLoading = false
                    )
                },
                onFailure = { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = throwable.message ?: "Error al obtener publicaciones"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class CreatePublicacionUiState(
    val titulo: String = "",
    val contenido: String = "",
    val imagenUrl: String = "",
    val tipoPublicacion: String = "GENERAL",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class CreatePublicacionViewModel @Inject constructor(
    private val createPublicationUseCase: CreatePublicationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePublicacionUiState())
    val uiState: StateFlow<CreatePublicacionUiState> = _uiState.asStateFlow()

    fun onTituloChange(titulo: String) {
        _uiState.value = _uiState.value.copy(titulo = titulo, error = null)
    }

    fun onContenidoChange(contenido: String) {
        _uiState.value = _uiState.value.copy(contenido = contenido, error = null)
    }

    fun onImagenUrlChange(imagenUrl: String) {
        _uiState.value = _uiState.value.copy(imagenUrl = imagenUrl, error = null)
    }

    fun onTipoPublicacionChange(tipo: String) {
        _uiState.value = _uiState.value.copy(tipoPublicacion = tipo, error = null)
    }

    fun crearPublicacion() {
        val state = _uiState.value
        if (state.titulo.isBlank()) {
            _uiState.value = state.copy(error = "El título es requerido")
            return
        }
        if (state.contenido.isBlank()) {
            _uiState.value = state.copy(error = "El contenido es requerido")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            createPublicationUseCase(
                titulo = state.titulo,
                contenido = state.contenido,
                imagenUrl = state.imagenUrl.takeIf { it.isNotBlank() },
                tipoPublicacion = state.tipoPublicacion
            ).fold(
                onSuccess = {
                    _uiState.value = CreatePublicacionUiState(isSuccess = true)
                },
                onFailure = { throwable ->
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = throwable.message ?: "Error al crear publicación"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
