package com.hugodev.red_up.features.publications.domain.repositories

import com.hugodev.red_up.features.publications.domain.entities.Publications

interface PublicationRepository {
    suspend fun getPublications(): Result<List<Publications>>

    suspend fun createPublication(
        titulo: String,
        contenido: String,
        imageBytes: ByteArray?,
        tipoPublicacion: String
    ): Result<Publications>

    suspend fun deletePublication(id: Long): Result<Unit>

    suspend fun editPublication(
        id: Long,
        titulo: String,
        contenido: String,
        tipoPublicacion: String
    ): Result<Publications>
}