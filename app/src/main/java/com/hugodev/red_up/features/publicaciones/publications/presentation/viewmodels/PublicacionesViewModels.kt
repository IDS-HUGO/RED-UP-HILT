package com.hugodev.red_up.features.publications.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hugodev.red_up.core.data.AuthPreferences
import com.hugodev.red_up.features.publications.domain.entities.Publications
import com.hugodev.red_up.features.publications.domain.usecases.CreatePublicationUseCase
import com.hugodev.red_up.features.publications.domain.usecases.DeletePublicationUseCase
import com.hugodev.red_up.features.publications.domain.usecases.EditPublicationUseCase
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
    val error: String? = null,
    val publicacionAEditar: Publications? = null,
    val mostrarDialogoEditar: Boolean = false,
    val currentUserId: Long? = null
)

@HiltViewModel
class PublicacionesListViewModel @Inject constructor(
    private val getPublicationUseCase: GetPublicationUseCase,
    private val deletePublicationUseCase: DeletePublicationUseCase,
    private val editPublicationUseCase: EditPublicationUseCase,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicacionesListUiState())
    val uiState: StateFlow<PublicacionesListUiState> = _uiState.asStateFlow()
    
    init {
        loadCurrentUser()
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            authPreferences.userIdFlow.collect { userId ->
                _uiState.value = _uiState.value.copy(currentUserId = userId)
            }
        }
    }

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

    fun deletePublicacion(id: Long) {
        viewModelScope.launch {
            deletePublicationUseCase(id).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        publicaciones = _uiState.value.publicaciones.filter { it.id != id }
                    )
                },
                onFailure = { throwable ->
                    _uiState.value = _uiState.value.copy(
                        error = throwable.message ?: "Error al eliminar"
                    )
                }
            )
        }
    }

    fun mostrarDialogoEditar(publicacion: Publications) {
        _uiState.value = _uiState.value.copy(
            publicacionAEditar = publicacion,
            mostrarDialogoEditar = true
        )
    }

    fun ocultarDialogoEditar() {
        _uiState.value = _uiState.value.copy(
            publicacionAEditar = null,
            mostrarDialogoEditar = false
        )
    }

    fun editarPublicacion(id: Long, titulo: String, contenido: String) {
        viewModelScope.launch {
            editPublicationUseCase(
                id = id,
                titulo = titulo,
                contenido = contenido,
                tipoPublicacion = "GENERAL"
            ).fold(
                onSuccess = { updated ->
                    _uiState.value = _uiState.value.copy(
                        publicaciones = _uiState.value.publicaciones.map {
                            if (it.id == updated.id) updated else it
                        },
                        mostrarDialogoEditar = false,
                        publicacionAEditar = null
                    )
                },
                onFailure = { throwable ->
                    _uiState.value = _uiState.value.copy(
                        error = throwable.message ?: "Error al editar"
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
    val imagenPreviewUri: String? = null,
    val tipoPublicacion: String = "GENERAL",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class CreatePublicacionViewModel @Inject constructor(
    private val createPublicationUseCase: CreatePublicationUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Claves para persistencia
    private companion object {
        const val KEY_TITULO = "create_pub_titulo"
        const val KEY_CONTENIDO = "create_pub_contenido"
        const val KEY_URI = "create_pub_uri"
    }

    private val _uiState = MutableStateFlow(
        CreatePublicacionUiState(
            titulo = savedStateHandle[KEY_TITULO] ?: "",
            contenido = savedStateHandle[KEY_CONTENIDO] ?: "",
            imagenPreviewUri = savedStateHandle[KEY_URI]
        )
    )
    val uiState: StateFlow<CreatePublicacionUiState> = _uiState.asStateFlow()
    private var imageBytes: ByteArray? = null

    fun onTituloChange(titulo: String) {
        savedStateHandle[KEY_TITULO] = titulo
        _uiState.value = _uiState.value.copy(titulo = titulo, error = null)
    }

    fun onContenidoChange(contenido: String) {
        savedStateHandle[KEY_CONTENIDO] = contenido
        _uiState.value = _uiState.value.copy(contenido = contenido, error = null)
    }

    fun onImagenCapturada(previewUri: String, bytes: ByteArray) {
        imageBytes = bytes
        savedStateHandle[KEY_URI] = previewUri
        _uiState.value = _uiState.value.copy(imagenPreviewUri = previewUri, error = null)
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
                imageBytes = imageBytes,
                tipoPublicacion = state.tipoPublicacion
            ).fold(
                onSuccess = {
                    imageBytes = null
                    clearSavedState()
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

    private fun clearSavedState() {
        savedStateHandle.remove<String>(KEY_TITULO)
        savedStateHandle.remove<String>(KEY_CONTENIDO)
        savedStateHandle.remove<String>(KEY_URI)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
