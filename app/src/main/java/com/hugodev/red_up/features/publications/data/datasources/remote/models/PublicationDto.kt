package com.hugodev.red_up.features.publications.data.datasources.remote.models

data class PublicationDto(
    val id: Int,
    val usuarioId: Int,
    val titulo: String,
    val contenido: String,
    val imagenUrl: String? = null,
    val carreraId: Int? = null,
    val tipoPublicacion: String,
    val activo: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val usuarioNombre: String = "",
    val usuarioApellido: String = "",
    val usuarioEmail: String = "",
    val totalLikes: Int = 0,
    val totalComentarios: Int = 0
)