package com.hugodev.red_up.features.publications.data.datasources.remote.api

import com.hugodev.red_up.features.publications.data.datasources.remote.models.ApiMessageDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.CommentDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.CreateCommentRequestDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.DeviceRegistrationRequestDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.DeviceTokenUpdateRequestDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.NotificationConfigDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.NotificationSummaryDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.CreatePublicationRequestDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.ProfileDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.PublicationDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.SyncEventsRequestDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.UpdateProfileRequestDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.UserStatsDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface UpRedApi {
    @GET("api/publicaciones")
    suspend fun getPublications(): List<PublicationDto>

    @POST("api/publicaciones")
    suspend fun createPublication(@Body request: CreatePublicationRequestDto): PublicationDto

    @Multipart
    @POST("api/publicaciones")
    suspend fun createPublicationWithImage(
        @Part("titulo") titulo: RequestBody,
        @Part("contenido") contenido: RequestBody,
        @Part("audiencia") audiencia: RequestBody,
        @Part files: MultipartBody.Part
    ): PublicationDto

    @DELETE("api/publicaciones/{id}")
    suspend fun deletePublication(@Path("id") id: Long)

    @PUT("api/publicaciones/{id}")
    suspend fun editPublication(
        @Path("id") id: Long,
        @Body request: CreatePublicationRequestDto
    ): PublicationDto

    @GET("api/publicaciones/{publicacionId}/comentarios")
    suspend fun getComments(
        @Path("publicacionId") publicacionId: Long,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 50
    ): List<CommentDto>

    @POST("api/publicaciones/{publicacionId}/comentarios")
    suspend fun addComment(@Path("publicacionId") publicacionId: Long, @Body request: CreateCommentRequestDto): CommentDto

    @DELETE("api/publicaciones/comentarios/{comentarioId}")
    suspend fun deleteComment(@Path("comentarioId") comentarioId: Long): ApiMessageDto

    @GET("api/usuarios/perfil/actual")
    suspend fun getCurrentProfile(): ProfileDto

    @GET("api/usuarios/{usuarioId}/perfil")
    suspend fun getUserProfile(@Path("usuarioId") userId: Long): ProfileDto

    @GET("api/usuarios/{usuarioId}/stats")
    suspend fun getUserStats(@Path("usuarioId") usuarioId: Long): UserStatsDto

    @Multipart
    @PUT("api/usuarios/perfil/actualizar")
    suspend fun updateCurrentProfile(
        @Part("biografia") biografia: RequestBody? = null,
        @Part("telefono") telefono: RequestBody? = null,
        @Part fotoPerfil: MultipartBody.Part? = null
    ): ProfileDto

    @POST("api/usuarios/{usuarioId}/seguir")
    suspend fun followUser(@Path("usuarioId") usuarioId: Long): ApiMessageDto

    @DELETE("api/usuarios/{usuarioId}/seguir")
    suspend fun unfollowUser(@Path("usuarioId") usuarioId: Long): ApiMessageDto

    @POST("api/notificaciones/dispositivos")
    suspend fun registerDevice(@Body request: DeviceRegistrationRequestDto): ApiMessageDto

    @PUT("api/notificaciones/dispositivos/token")
    suspend fun updateDeviceToken(@Body request: DeviceTokenUpdateRequestDto): ApiMessageDto

    @GET("api/notificaciones/configuracion")
    suspend fun getNotificationConfig(): NotificationConfigDto

    @PUT("api/notificaciones/configuracion")
    suspend fun updateNotificationConfig(@Body config: NotificationConfigDto): NotificationConfigDto

    @POST("api/notificaciones/eventos/sync")
    suspend fun syncDeferredEvents(@Body request: SyncEventsRequestDto): ApiMessageDto

    @GET("api/notificaciones/resumen")
    suspend fun getNotificationSummary(): NotificationSummaryDto
}
