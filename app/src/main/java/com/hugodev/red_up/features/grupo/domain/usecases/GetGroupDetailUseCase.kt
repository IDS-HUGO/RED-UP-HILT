package com.hugodev.red_up.features.groups.domain.usecases

import com.hugodev.red_up.features.groups.domain.entities.GroupDetail
import com.hugodev.red_up.features.groups.domain.repositories.GroupRepository
import javax.inject.Inject

class GetGroupDetailUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(groupId: Long): Result<GroupDetail> {
        return groupRepository.getGroupDetail(groupId)
    }
}
