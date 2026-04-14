package com.hugodev.red_up.features.publications.data.datasources.remote.models

import com.google.gson.annotations.SerializedName

data class ApiMessageDto(
    @SerializedName("message") val message: String
)

data class CommentUserDto(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido_paterno") val apellidoPaterno: String,
    @SerializedName("apellido_materno") val apellidoMaterno: String? = null,
    @SerializedName("foto_perfil_url") val fotoPerfilUrl: String? = null
)

data class CommentDto(
    @SerializedName("id") val id: Long,
    @SerializedName("contenido") val contenido: String,
    @SerializedName("usuario_id") val usuarioId: Long,
    @SerializedName("creado_en") val creadoEn: String,
    @SerializedName("usuario") val usuario: CommentUserDto? = null
)

data class CreateCommentRequestDto(
    @SerializedName("publicacion_id") val publicacionId: Long,
    @SerializedName("contenido") val contenido: String,
    @SerializedName("comentario_padre_id") val comentarioPadreId: Long? = null
)

data class ProfileDto(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido_paterno") val apellidoPaterno: String,
    @SerializedName("apellido_materno") val apellidoMaterno: String? = null,
    @SerializedName("correo_institucional") val correoInstitucional: String,
    @SerializedName("foto_perfil_url") val fotoPerfilUrl: String? = null,
    @SerializedName("biografia") val biografia: String? = null,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("carrera") val carrera: CarreraDto? = null,
    @SerializedName("cuatrimestre") val cuatrimestre: CuatrimestreDto? = null
)

data class CarreraDto(
    @SerializedName("nombre") val nombre: String? = null
)

data class CuatrimestreDto(
    @SerializedName("numero") val numero: Int? = null
)

data class UserStatsDto(
    @SerializedName("total_seguidores") val totalSeguidores: Int = 0,
    @SerializedName("total_siguiendo") val totalSiguiendo: Int = 0,
    @SerializedName("total_publicaciones") val totalPublicaciones: Int = 0,
    @SerializedName("total_comentarios") val totalComentarios: Int = 0,
    @SerializedName("total_reacciones") val totalReacciones: Int = 0
)

data class UpdateProfileRequestDto(
    @SerializedName("biografia") val biografia: String? = null,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("foto_perfil_url") val fotoPerfilUrl: String? = null
)

data class DeviceRegistrationRequestDto(
    @SerializedName("uuid_dispositivo") val uuidDispositivo: String,
    @SerializedName("plataforma") val plataforma: String = "android",
    @SerializedName("token_push") val tokenPush: String? = null
)

data class DeviceTokenUpdateRequestDto(
    @SerializedName("uuid_dispositivo") val uuidDispositivo: String,
    @SerializedName("token_push") val tokenPush: String
)

data class NotificationConfigDto(
    @SerializedName("push_enabled") val pushEnabled: Boolean = true,
    @SerializedName("chat_enabled") val chatEnabled: Boolean = true,
    @SerializedName("groups_enabled") val groupsEnabled: Boolean = true,
    @SerializedName("social_enabled") val socialEnabled: Boolean = true,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class NotificationSummaryDto(
    @SerializedName("total_no_leidas") val totalNoLeidas: Int = 0,
    @SerializedName("last_notification_at") val lastNotificationAt: String? = null
)

data class SyncEventDto(
    @SerializedName("event_type") val eventType: String,
    @SerializedName("payload") val payload: Map<String, Any?> = emptyMap(),
    @SerializedName("created_at") val createdAt: Long
)

data class SyncEventsRequestDto(
    @SerializedName("events") val events: List<SyncEventDto>
)
