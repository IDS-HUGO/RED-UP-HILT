package com.hugodev.red_up.features.groups.domain.usecases

import com.hugodev.red_up.features.groups.domain.repositories.GroupRepository
import javax.inject.Inject

class InviteMemberUseCase @Inject constructor(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: Long, userId: Long): Result<String> {
        return repository.inviteMember(groupId, userId)
    }
}
