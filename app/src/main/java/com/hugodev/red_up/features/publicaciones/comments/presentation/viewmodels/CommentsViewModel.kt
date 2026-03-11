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
class CommentsViewModel @Inject constructor() : ViewModel() {

    private val _commentsState = MutableStateFlow(CommentsUiState())
    val commentsState: StateFlow<CommentsUiState> = _commentsState

    fun loadComments(publicacionId: Long, page: Int = 0) {
        viewModelScope.launch {
            _commentsState.value = _commentsState.value.copy(isLoading = true)
            try {
                // TODO: Implementar llamada a GET /api/comentarios/publicaciones/{publicacionId}
                // val response = apiService.getComments(publicacionId, skip = page * 20, limit = 20)
                // Simulación por ahora
                _commentsState.value = _commentsState.value.copy(
                    comentarios = listOf(
                        Comment(
                            id = 1,
                            contenido = "¡Excelente publicación!",
                            usuarioId = 2,
                            usuarioNombre = "Juan",
                            usuarioApellido = "Pérez",
                            usuarioFoto = null,
                            creadoEn = "Hace 2 horas"
                        ),
                        Comment(
                            id = 2,
                            contenido = "Me encanta este contenido",
                            usuarioId = 3,
                            usuarioNombre = "Maria",
                            usuarioApellido = "García",
                            usuarioFoto = null,
                            creadoEn = "Hace 1 hora"
                        )
                    ),
                    totalComentarios = 2,
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
            _commentsState.value = _commentsState.value.copy(estaEscribiendo = true)
            try {
                // TODO: Implementar llamada POST a /api/comentarios/publicaciones/{publicacionId}
                val nuevoComentario = Comment(
                    id = System.currentTimeMillis(),
                    contenido = contenido,
                    usuarioId = 1,
                    usuarioNombre = "Tu Nombre",
                    usuarioApellido = "Tu Apellido",
                    usuarioFoto = null,
                    creadoEn = "Ahora"
                )

                val nuevaLista = listOf(nuevoComentario) + _commentsState.value.comentarios
                _commentsState.value = _commentsState.value.copy(
                    comentarios = nuevaLista,
                    totalComentarios = _commentsState.value.totalComentarios + 1,
                    estaEscribiendo = false,
                    success = "Comentario publicado"
                )
            } catch (e: Exception) {
                Log.e("CommentsViewModel", "Error adding comment", e)
                _commentsState.value = _commentsState.value.copy(
                    error = "Error al publicar comentario",
                    estaEscribiendo = false
                )
            }
        }
    }

    fun deleteComment(commentId: Long) {
        viewModelScope.launch {
            try {
                // TODO: Implementar llamada DELETE a /api/comentarios/{commentId}
                val comentariosActualizados = _commentsState.value.comentarios.filter { it.id != commentId }
                _commentsState.value = _commentsState.value.copy(
                    comentarios = comentariosActualizados,
                    totalComentarios = _commentsState.value.totalComentarios - 1,
                    success = "Comentario eliminado"
                )
            } catch (e: Exception) {
                Log.e("CommentsViewModel", "Error deleting comment", e)
                _commentsState.value = _commentsState.value.copy(
                    error = "Error al eliminar comentario"
                )
            }
        }
    }

    fun updateComment(commentId: Long, nuevoContenido: String) {
        viewModelScope.launch {
            try {
                // TODO: Implementar llamada PUT a /api/comentarios/{commentId}
                val comentariosActualizados = _commentsState.value.comentarios.map { comment ->
                    if (comment.id == commentId) {
                        comment.copy(contenido = nuevoContenido)
                    } else {
                        comment
                    }
                }
                _commentsState.value = _commentsState.value.copy(
                    comentarios = comentariosActualizados,
                    success = "Comentario actualizado"
                )
            } catch (e: Exception) {
                Log.e("CommentsViewModel", "Error updating comment", e)
                _commentsState.value = _commentsState.value.copy(
                    error = "Error al actualizar comentario"
                )
            }
        }
    }

    fun loadReplies(commentId: Long) {
        viewModelScope.launch {
            try {
                // TODO: Implementar llamada GET a /api/comentarios/{commentId}/respuestas
                Log.d("CommentsViewModel", "Loading replies for comment $commentId")
            } catch (e: Exception) {
                Log.e("CommentsViewModel", "Error loading replies", e)
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
