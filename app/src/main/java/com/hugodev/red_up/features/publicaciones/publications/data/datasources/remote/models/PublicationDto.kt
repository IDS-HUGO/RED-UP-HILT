package com.hugodev.red_up.features.publications.data.datasources.remote.models

import com.google.gson.annotations.SerializedName

data class PublicationAuthorDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido_paterno") val apellidoPaterno: String,
    @SerializedName("apellido_materno") val apellidoMaterno: String? = null,
    @SerializedName("foto_perfil_url") val fotoPerfilUrl: String? = null
)

data class PublicationMediaDto(
    @SerializedName("id") val id: Long,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("url_archivo") val urlArchivo: String
)

data class PublicationDto(
    @SerializedName("id") val id: Long,
    @SerializedName("autor_id") val autorId: Long,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("contenido") val contenido: String,
    @SerializedName("imagen_url") val imagenUrl: String? = null,
    @SerializedName("audiencia") val audiencia: String,
    @SerializedName("publicada_en") val publicadaEn: String,
    @SerializedName("autor") val autor: PublicationAuthorDto? = null,
    @SerializedName("multimedia") val multimedia: List<PublicationMediaDto> = emptyList(),
    @SerializedName("total_reacciones") val totalReacciones: Int = 0,
    @SerializedName("total_comentarios") val totalComentarios: Int = 0
)