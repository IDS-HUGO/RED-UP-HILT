package com.hugodev.red_up.features.groups.data.datasources.remote.api

import com.hugodev.red_up.features.groups.data.datasources.remote.models.CreateGroupRequestDto
import com.hugodev.red_up.features.groups.data.datasources.remote.models.GroupDetailDto
import com.hugodev.red_up.features.groups.data.datasources.remote.models.GroupDto
import com.hugodev.red_up.features.groups.data.datasources.remote.models.UserSearchDto
import com.hugodev.red_up.features.groups.data.datasources.remote.models.InviteMemberResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GroupsApi {
    @GET("api/grupos/mis-grupos")
    suspend fun getMyGroups(): List<GroupDto>

    @GET("api/grupos/buscar")
    suspend fun searchGroups(
        @Query("query") query: String
    ): List<GroupDto>

    @GET("api/grupos/{grupo_id}")
    suspend fun getGroupDetail(
        @Path("grupo_id") groupId: Long
    ): GroupDetailDto

    @POST("api/grupos/")
    suspend fun createGroup(
        @Body request: CreateGroupRequestDto
    ): GroupDto

    @POST("api/grupos/{grupo_id}/unirse")
    suspend fun joinGroup(
        @Path("grupo_id") groupId: Long
    ): Map<String, String>

    @GET("api/usuarios/buscar")
    suspend fun searchUsers(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20
    ): List<UserSearchDto>

    @POST("api/grupos/{grupo_id}/miembros/{usuario_id}/invitar")
    suspend fun inviteMember(
        @Path("grupo_id") groupId: Long,
        @Path("usuario_id") userId: Long
    ): InviteMemberResponseDto
}
