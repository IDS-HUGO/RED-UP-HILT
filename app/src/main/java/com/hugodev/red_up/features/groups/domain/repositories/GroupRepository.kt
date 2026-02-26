package com.hugodev.red_up.features.groups.domain.repositories

import com.hugodev.red_up.features.groups.domain.entities.Group
import com.hugodev.red_up.features.groups.domain.entities.GroupDetail

interface GroupRepository {
    suspend fun getMyGroups(): Result<List<Group>>
    suspend fun searchGroups(query: String): Result<List<Group>>
    suspend fun getGroupDetail(groupId: Long): Result<GroupDetail>
    suspend fun createGroup(
        nombre: String,
        descripcion: String?,
        carreraId: Long,
        privacidad: String
    ): Result<Group>
    suspend fun joinGroup(groupId: Long): Result<String>
}
