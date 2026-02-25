package com.hugodev.red_up.features.publications.data.datasources.remote.api

import com.hugodev.red_up.features.publications.data.datasources.remote.models.CreatePublicationRequestDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.PublicationDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UpRedApi {
    @GET("api/publicaciones")
    suspend fun getPublications(): List<PublicationDto>

    @POST("api/publicaciones")
    suspend fun createPublication(
        @Body request: CreatePublicationRequestDto
    ): PublicationDto
}