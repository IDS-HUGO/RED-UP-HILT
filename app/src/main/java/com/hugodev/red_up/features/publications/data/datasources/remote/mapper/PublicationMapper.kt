package com.hugodev.red_up.features.publications.data.datasources.remote.mapper

import com.hugodev.red_up.features.publications.data.datasources.remote.models.PublicationDto
import com.hugodev.red_up.features.publications.domain.entities.Publications


fun PublicationDto.toDomain(): Publications {
    return Publications(
        id = this.id,
        usuarioId = this.usuarioId,
        titulo = this.titulo,
        contenido = this.contenido,
        imagenUrl = this.imagenUrl,
        carreraId = this.carreraId,
        tipoPublicacion = this.tipoPublicacion,
        activo = this.activo,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        usuarioNombre = this.usuarioNombre,
        usuarioApellido = this.usuarioApellido,
        usuarioEmail = this.usuarioEmail,
        totalLikes = this.totalLikes,
        totalComentarios = this.totalComentarios
    )
}