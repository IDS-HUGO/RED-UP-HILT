package com.hugodev.red_up.features.publications.domain.usecases

import com.hugodev.red_up.features.publications.domain.repositories.PublicationRepository
import javax.inject.Inject

class DeletePublicationUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return publicationRepository.deletePublication(id)
    }
}
