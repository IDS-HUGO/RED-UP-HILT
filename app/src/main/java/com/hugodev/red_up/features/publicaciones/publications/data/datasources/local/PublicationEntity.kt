package com.hugodev.red_up.features.publications.data.datasources.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hugodev.red_up.features.publications.domain.entities.Publications

@Entity(tableName = "publicaciones_cache")
data class PublicationEntity(
    @PrimaryKey val id: Long,
    val autorId: Long,
    val titulo: String,
    val contenido: String,
    val imagenUrl: String?,
    val audiencia: String,
    val publicadaEn: String,
    val autorNombre: String,
    val autorApellido: String,
    val autorFotoUrl: String?,
    val totalReacciones: Int,
    val totalComentarios: Int
)

fun PublicationEntity.toDomain(): Publications = Publications(
    id = id,
    autorId = autorId,
    titulo = titulo,
    contenido = contenido,
    imagenUrl = imagenUrl,
    audiencia = audiencia,
    publicadaEn = publicadaEn,
    autorNombre = autorNombre,
    autorApellido = autorApellido,
    autorFotoUrl = autorFotoUrl,
    totalReacciones = totalReacciones,
    totalComentarios = totalComentarios
)

fun Publications.toEntity(): PublicationEntity = PublicationEntity(
    id = id,
    autorId = autorId,
    titulo = titulo,
    contenido = contenido,
    imagenUrl = imagenUrl,
    audiencia = audiencia,
    publicadaEn = publicadaEn,
    autorNombre = autorNombre,
    autorApellido = autorApellido,
    autorFotoUrl = autorFotoUrl,
    totalReacciones = totalReacciones,
    totalComentarios = totalComentarios
)
