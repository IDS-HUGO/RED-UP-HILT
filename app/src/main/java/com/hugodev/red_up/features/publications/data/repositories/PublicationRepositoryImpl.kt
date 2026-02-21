package com.hugodev.red_up.features.publications.data.repositories

import com.hugodev.red_up.features.publications.data.datasources.remote.api.UpRedApi
import com.hugodev.red_up.features.publications.data.datasources.remote.mapper.toDomain
import com.hugodev.red_up.features.publications.data.datasources.remote.models.CreatePublicationRequestDto
import com.hugodev.red_up.features.publications.domain.entities.Publications
import com.hugodev.red_up.features.publications.domain.repositories.PublicationRepository
import javax.inject.Inject

class PublicationRepositoryImpl @Inject constructor(
    private val upRedApi: UpRedApi
) : PublicationRepository {

    override suspend fun getPublications(): Result<List<Publications>> {
        return runCatching {
            upRedApi.getPublications().map { it.toDomain() }
        }
    }

    override suspend fun createPublication(
        titulo: String,
        contenido: String,
        imagenUrl: String?,
        tipoPublicacion: String
    ): Result<Publications> {
        return runCatching {
            upRedApi.createPublication(
                CreatePublicationRequestDto(
                    titulo = titulo,
                    contenido = contenido,
                    imagenUrl = imagenUrl,
                    tipoPublicacion = tipoPublicacion
                )
            ).toDomain()
        }
    }
}
