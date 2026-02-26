package com.hugodev.red_up.features.groups.data.datasources.remote.api

import com.hugodev.red_up.features.groups.data.datasources.remote.models.CreateGroupRequestDto
import com.hugodev.red_up.features.groups.data.datasources.remote.models.GroupDetailDto
import com.hugodev.red_up.features.groups.data.datasources.remote.models.GroupDto
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

    @POST("api/grupos")
    suspend fun createGroup(
        @Body request: CreateGroupRequestDto
    ): GroupDto

    @POST("api/grupos/{grupo_id}/unirse")
    suspend fun joinGroup(
        @Path("grupo_id") groupId: Long
    ): Map<String, String>
}
