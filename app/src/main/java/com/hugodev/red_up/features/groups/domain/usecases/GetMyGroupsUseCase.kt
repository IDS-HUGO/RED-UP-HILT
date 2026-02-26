package com.hugodev.red_up.features.groups.domain.usecases

import com.hugodev.red_up.features.groups.domain.entities.Group
import com.hugodev.red_up.features.groups.domain.repositories.GroupRepository
import javax.inject.Inject

class GetMyGroupsUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(): Result<List<Group>> {
        return groupRepository.getMyGroups()
    }
}
