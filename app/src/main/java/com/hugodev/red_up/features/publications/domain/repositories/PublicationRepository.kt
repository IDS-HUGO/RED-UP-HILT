package com.hugodev.red_up.features.publications.domain.repositories

import com.hugodev.red_up.features.publications.domain.entities.Publications

interface PublicationRepository {
    suspend fun getPublications(): Result<List<Publications>>

    suspend fun createPublication(
        titulo: String,
        contenido: String,
        imagenUrl: String?,
        tipoPublicacion: String
    ): Result<Publications>
}