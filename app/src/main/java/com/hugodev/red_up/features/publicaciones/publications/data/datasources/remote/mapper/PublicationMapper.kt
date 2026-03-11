package com.hugodev.red_up.features.publications.data.datasources.remote.mapper

import com.hugodev.red_up.BuildConfig
import com.hugodev.red_up.features.publications.data.datasources.remote.models.PublicationDto
import com.hugodev.red_up.features.publications.domain.entities.Publications


private fun resolveAbsoluteUrl(url: String?): String? {
    if (url.isNullOrBlank()) return null
    if (url.startsWith("http://") || url.startsWith("https://")) return url
    val baseUrl = BuildConfig.BASE_URL_UPRED.trimEnd('/')
    return if (url.startsWith('/')) "$baseUrl$url" else "$baseUrl/$url"
}

fun PublicationDto.toDomain(): Publications {
    val firstImageUrl = multimedia.firstOrNull { it.tipo.equals("imagen", true) }?.urlArchivo
    val normalizedImageUrl = resolveAbsoluteUrl(imagenUrl ?: firstImageUrl)

    return Publications(
        id = this.id,
        autorId = this.autorId,
        titulo = this.titulo,
        contenido = this.contenido,
        imagenUrl = normalizedImageUrl,
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