package com.hugodev.red_up.features.publications.domain.usecases

import com.hugodev.red_up.features.publications.domain.entities.Publications
import com.hugodev.red_up.features.publications.domain.repositories.PublicationRepository
import javax.inject.Inject

class CreatePublicationUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    suspend operator fun invoke(
        titulo: String,
        contenido: String,
        imageBytes: ByteArray?,
        tipoPublicacion: String
    ): Result<Publications> {
        return publicationRepository.createPublication(
            titulo = titulo,
            contenido = contenido,
            imageBytes = imageBytes,
            tipoPublicacion = tipoPublicacion
        )
    }

}