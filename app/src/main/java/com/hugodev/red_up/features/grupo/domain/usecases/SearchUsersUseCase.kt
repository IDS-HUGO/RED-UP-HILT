package com.hugodev.red_up.features.groups.domain.usecases

import com.hugodev.red_up.features.groups.domain.entities.User
import com.hugodev.red_up.features.groups.domain.repositories.GroupRepository
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(query: String): Result<List<User>> {
        if (query.isBlank()) {
            return Result.success(emptyList())
        }
        return repository.searchUsers(query)
    }
}
