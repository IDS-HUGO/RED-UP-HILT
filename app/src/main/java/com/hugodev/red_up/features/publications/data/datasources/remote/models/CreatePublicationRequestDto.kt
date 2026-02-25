package com.hugodev.red_up.features.publications.data.datasources.remote.models

import com.google.gson.annotations.SerializedName

data class CreatePublicationRequestDto(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("contenido") val contenido: String,
    @SerializedName("audiencia") val audiencia: String,
    @SerializedName("carrera_objetivo_id") val carreraObjetivoId: Long? = null,
    @SerializedName("cuatrimestre_objetivo_id") val cuatrimestreObjetivoId: Long? = null,
    @SerializedName("tipo_publicacion_id") val tipoPublicacionId: Long? = null,
    @SerializedName("permite_comentarios") val permiteComentarios: Boolean = true,
    @SerializedName("es_anonima") val esAnonima: Boolean = false
)
