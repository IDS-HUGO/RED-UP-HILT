package com.hugodev.red_up.features.groups.domain.entities

data class Group(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val carreraId: Long,
    val privacidad: String,
    val creadoEn: String,
    val totalMiembros: Int
)

data class GroupDetail(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val carreraId: Long,
    val privacidad: String,
    val creadoEn: String,
    val totalMiembros: Int,
    val miembros: List<GroupMember>
)

data class GroupMember(
    val usuarioId: Long,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String?,
    val fotoPerfilUrl: String?,
    val rolMiembro: String,
    val estadoMembresia: String
)
