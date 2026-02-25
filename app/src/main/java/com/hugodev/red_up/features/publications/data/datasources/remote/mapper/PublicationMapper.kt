package com.hugodev.red_up.features.publications.data.datasources.remote.mapper

import com.hugodev.red_up.features.publications.data.datasources.remote.models.PublicationDto
import com.hugodev.red_up.features.publications.domain.entities.Publications


fun PublicationDto.toDomain(): Publications {
    return Publications(
        id = this.id,
        autorId = this.autorId,
        titulo = this.titulo,
        contenido = this.contenido,
        audiencia = this.audiencia,
        publicadaEn = this.publicadaEn,
        autorNombre = this.autor?.nombre.orEmpty(),
        autorApellido = listOfNotNull(
            this.autor?.apellidoPaterno,
            this.autor?.apellidoMaterno
        ).joinToString(" "),
        autorFotoUrl = this.autor?.fotoPerfilUrl,
        totalReacciones = this.totalReacciones,
        totalComentarios = this.totalComentarios
    )
}