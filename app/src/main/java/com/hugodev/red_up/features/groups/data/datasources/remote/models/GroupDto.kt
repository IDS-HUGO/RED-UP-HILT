package com.hugodev.red_up.features.groups.data.datasources.remote.models

import com.google.gson.annotations.SerializedName

data class GroupMemberDto(
    @SerializedName("usuario_id") val usuarioId: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido_paterno") val apellidoPaterno: String,
    @SerializedName("apellido_materno") val apellidoMaterno: String? = null,
    @SerializedName("foto_perfil_url") val fotoPerfilUrl: String? = null,
    @SerializedName("rol_miembro") val rolMiembro: String,
    @SerializedName("estado_membresia") val estadoMembresia: String
)

data class GroupDto(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("carrera_id") val carreraId: Long,
    @SerializedName("privacidad") val privacidad: String,
    @SerializedName("creado_en") val creadoEn: String,
    @SerializedName("total_miembros") val totalMiembros: Int = 0
)

data class CreateGroupRequestDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("carrera_id") val carreraId: Long,
    @SerializedName("privacidad") val privacidad: String
)

data class GroupDetailDto(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("carrera_id") val carreraId: Long,
    @SerializedName("privacidad") val privacidad: String,
    @SerializedName("creado_en") val creadoEn: String,
    @SerializedName("total_miembros") val totalMiembros: Int = 0,
    @SerializedName("miembros") val miembros: List<GroupMemberDto>? = null
)

data class UserSearchDto(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido_paterno") val apellidoPaterno: String,
    @SerializedName("apellido_materno") val apellidoMaterno: String? = null,
    @SerializedName("email") val email: String,
    @SerializedName("foto_perfil_url") val fotoPerfilUrl: String? = null,
    @SerializedName("carrera_id") val carreraId: Long? = null,
    @SerializedName("cuatrimestre_id") val cuatrimestreId: Long? = null
)

data class InviteMemberResponseDto(
    @SerializedName("message") val message: String
)
