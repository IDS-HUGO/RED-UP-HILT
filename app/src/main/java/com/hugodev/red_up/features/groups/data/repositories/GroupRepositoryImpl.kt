package com.hugodev.red_up.features.groups.data.repositories

import com.hugodev.red_up.features.groups.data.datasources.remote.api.GroupsApi
import com.hugodev.red_up.features.groups.data.datasources.remote.mapper.toDomain
import com.hugodev.red_up.features.groups.data.datasources.remote.models.CreateGroupRequestDto
import com.hugodev.red_up.features.groups.domain.entities.Group
import com.hugodev.red_up.features.groups.domain.entities.GroupDetail
import com.hugodev.red_up.features.groups.domain.entities.User
import com.hugodev.red_up.features.groups.domain.repositories.GroupRepository
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val groupsApi: GroupsApi
) : GroupRepository {

    override suspend fun getMyGroups(): Result<List<Group>> {
        return runCatching {
            groupsApi.getMyGroups().map { it.toDomain() }
        }
    }

    override suspend fun searchGroups(query: String): Result<List<Group>> {
        return runCatching {
            groupsApi.searchGroups(query).map { it.toDomain() }
        }
    }

    override suspend fun getGroupDetail(groupId: Long): Result<GroupDetail> {
        return runCatching {
            groupsApi.getGroupDetail(groupId).toDomain()
        }
    }

    override suspend fun createGroup(
        nombre: String,
        descripcion: String?,
        carreraId: Long,
        privacidad: String
    ): Result<Group> {
        return runCatching {
            groupsApi.createGroup(
                CreateGroupRequestDto(
                    nombre = nombre,
                    descripcion = descripcion,
                    carreraId = carreraId,
                    privacidad = privacidad
                )
            ).toDomain()
        }
    }

    override suspend fun joinGroup(groupId: Long): Result<String> {
        return runCatching {
            val response = groupsApi.joinGroup(groupId)
            response["message"] ?: "Operación exitosa"
        }
    }

    override suspend fun searchUsers(query: String): Result<List<User>> {
        return runCatching {
            groupsApi.searchUsers(query).map { it.toDomain() }
        }
    }

    override suspend fun inviteMember(groupId: Long, userId: Long): Result<String> {
        return runCatching {
            val response = groupsApi.inviteMember(groupId, userId)
            response.message
        }
    }
}
