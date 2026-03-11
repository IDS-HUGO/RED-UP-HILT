package com.hugodev.red_up.features.publications.data.datasources.remote.api

import com.hugodev.red_up.features.publications.data.datasources.remote.models.CreatePublicationRequestDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.PublicationDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.GET
import retrofit2.http.Part
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UpRedApi {
    @GET("api/publicaciones")
    suspend fun getPublications(): List<PublicationDto>

    @POST("api/publicaciones")
    suspend fun createPublication(
        @Body request: CreatePublicationRequestDto
    ): PublicationDto

    @Multipart
    @POST("api/publicaciones")
    suspend fun createPublicationWithImage(
        @Part("titulo") titulo: RequestBody,
        @Part("contenido") contenido: RequestBody,
        @Part("audiencia") audiencia: RequestBody,
        @Part files: MultipartBody.Part
    ): PublicationDto

    @DELETE("api/publicaciones/{id}")
    suspend fun deletePublication(
        @Path("id") id: Long
    )

    @PUT("api/publicaciones/{id}")
    suspend fun editPublication(
        @Path("id") id: Long,
        @Body request: CreatePublicationRequestDto
    ): PublicationDto
}