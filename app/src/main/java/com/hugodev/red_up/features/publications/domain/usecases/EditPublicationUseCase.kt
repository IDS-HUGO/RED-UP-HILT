package com.hugodev.red_up.features.publications.domain.usecases

import com.hugodev.red_up.features.publications.domain.entities.Publications
import com.hugodev.red_up.features.publications.domain.repositories.PublicationRepository
import javax.inject.Inject

class EditPublicationUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    suspend operator fun invoke(
        id: Long,
        titulo: String,
        contenido: String,
        tipoPublicacion: String
    ): Result<Publications> {
        return publicationRepository.editPublication(
            id = id,
            titulo = titulo,
            contenido = contenido,
            tipoPublicacion = tipoPublicacion
        )
    }
}
