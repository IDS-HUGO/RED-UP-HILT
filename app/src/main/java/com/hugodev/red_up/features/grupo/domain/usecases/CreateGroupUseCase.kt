package com.hugodev.red_up.features.groups.domain.usecases

import com.hugodev.red_up.features.groups.domain.entities.Group
import com.hugodev.red_up.features.groups.domain.repositories.GroupRepository
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(
        nombre: String,
        descripcion: String?,
        carreraId: Long,
        privacidad: String
    ): Result<Group> {
        return groupRepository.createGroup(
            nombre = nombre,
            descripcion = descripcion,
            carreraId = carreraId,
            privacidad = privacidad
        )
    }
}
