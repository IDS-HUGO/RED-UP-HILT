package com.hugodev.red_up.features.publicaciones.comments.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import javax.inject.Inject

data class Comment(
    val id: Long,
    val contenido: String,
    val usuarioId: Long,
    val usuarioNombre: String,
    val usuarioApellido: String,
    val usuarioFoto: String?,
    val creadoEn: String,
    val respuestas: List<Comment> = emptyList()
)

data class CommentsUiState(
    val isLoading: Boolean = false,
    val comentarios: List<Comment> = emptyList(),
    val comentariosPaginados: List<Comment> = emptyList(),
    val totalComentarios: Int = 0,
    val estaEscribiendo: Boolean = false,
    val error: String? = null,
    val success: String? = null,
    val currentPage: Int = 0
)

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val upRedApi: com.hugodev.red_up.features.publications.data.datasources.remote.api.UpRedApi
) : ViewModel() {

    private val _commentsState = MutableStateFlow(CommentsUiState())
    val commentsState: StateFlow<CommentsUiState> = _commentsState

    fun loadComments(publicacionId: Long, page: Int = 0) {
        viewModelScope.launch {
            _commentsState.value = _commentsState.value.copy(isLoading = true, error = null)
            try {
                val response = upRedApi.getComments(publicacionId, skip = page * 20, limit = 20)
                val mapped = response.map { dto ->
                    Comment(
                        id = dto.id,
                        contenido = dto.contenido,
                        usuarioId = dto.usuarioId,
                        usuarioNombre = dto.usuario?.nombre.orEmpty(),
                        usuarioApellido = listOfNotNull(dto.usuario?.apellidoPaterno, dto.usuario?.apellidoMaterno)
                            .joinToString(" "),
                        usuarioFoto = dto.usuario?.fotoPerfilUrl,
                        creadoEn = dto.creadoEn
                    )
                }
                _commentsState.value = _commentsState.value.copy(
                    comentarios = mapped,
                    totalComentarios = mapped.size,
                    currentPage = page,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e("CommentsViewModel", "Error loading comments", e)
                _commentsState.value = _commentsState.value.copy(
                    error = "Error al cargar comentarios: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun addComment(publicacionId: Long, contenido: String) {
        viewModelScope.launch {
            _commentsState.value = _commentsState.value.copy(estaEscribiendo = true, error = null)
            try {
                upRedApi.addComment(
                    publicacionId = publicacionId,
                    request = com.hugodev.red_up.features.publications.data.datasources.remote.models.CreateCommentRequestDto(
                        publicacionId = publicacionId,
                        contenido = contenido
                    )
                )

                loadComments(publicacionId, _commentsState.value.currentPage)
                _commentsState.value = _commentsState.value.copy(
                    estaEscribiendo = false,
                    success = "Comentario publicado"
                )
            } catch (e: Exception) {
                Log.e("CommentsViewModel", "Error adding comment", e)
                _commentsState.value = _commentsState.value.copy(
                    error = "Error al publicar comentario: ${e.message}",
                    estaEscribiendo = false
                )
            }
        }
    }

    fun deleteComment(publicacionId: Long, commentId: Long) {
        viewModelScope.launch {
            try {
                upRedApi.deleteComment(commentId)
                loadComments(publicacionId, _commentsState.value.currentPage)
                _commentsState.value = _commentsState.value.copy(
                    success = "Comentario eliminado"
                )
            } catch (e: Exception) {
                Log.e("CommentsViewModel", "Error deleting comment", e)
                _commentsState.value = _commentsState.value.copy(
                    error = "Error al eliminar comentario: ${e.message}"
                )
            }
        }
    }

    fun clearMessages() {
        _commentsState.value = _commentsState.value.copy(
            error = null,
            success = null
        )
    }
}
