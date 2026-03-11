package com.hugodev.red_up.features.groups.data.datasources.remote.mapper

import com.hugodev.red_up.features.groups.data.datasources.remote.models.GroupDetailDto
import com.hugodev.red_up.features.groups.data.datasources.remote.models.GroupDto
import com.hugodev.red_up.features.groups.data.datasources.remote.models.GroupMemberDto
import com.hugodev.red_up.features.groups.data.datasources.remote.models.UserSearchDto
import com.hugodev.red_up.features.groups.domain.entities.Group
import com.hugodev.red_up.features.groups.domain.entities.GroupDetail
import com.hugodev.red_up.features.groups.domain.entities.GroupMember
import com.hugodev.red_up.features.groups.domain.entities.User

fun GroupDto.toDomain(): Group {
    return Group(
        id = this.id,
        nombre = this.nombre,
        descripcion = this.descripcion.orEmpty(),
        carreraId = this.carreraId,
        privacidad = this.privacidad,
        creadoEn = this.creadoEn,
        totalMiembros = this.totalMiembros
    )
}

fun GroupDetailDto.toDomain(): GroupDetail {
    return GroupDetail(
        id = this.id,
        nombre = this.nombre,
        descripcion = this.descripcion.orEmpty(),
        carreraId = this.carreraId,
        privacidad = this.privacidad,
        creadoEn = this.creadoEn,
        totalMiembros = this.totalMiembros,
        miembros = this.miembros?.map { it.toDomain() } ?: emptyList()
    )
}

fun GroupMemberDto.toDomain(): GroupMember {
    return GroupMember(
        usuarioId = this.usuarioId,
        nombre = this.nombre,
        apellidoPaterno = this.apellidoPaterno,
        apellidoMaterno = this.apellidoMaterno,
        fotoPerfilUrl = this.fotoPerfilUrl,
        rolMiembro = this.rolMiembro,
        estadoMembresia = this.estadoMembresia
    )
}

fun UserSearchDto.toDomain(): User {
    return User(
        id = this.id,
        nombre = this.nombre,
        apellidoPaterno = this.apellidoPaterno,
        apellidoMaterno = this.apellidoMaterno,
        email = this.email ?: "",
        fotoPerfilUrl = this.fotoPerfilUrl,
        carreraId = this.carreraId,
        cuatrimestreId = this.cuatrimestreId
    )
}
