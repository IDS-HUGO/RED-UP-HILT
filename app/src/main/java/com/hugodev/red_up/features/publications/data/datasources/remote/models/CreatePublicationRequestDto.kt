package com.hugodev.red_up.features.publications.data.datasources.remote.models

data class CreatePublicationRequestDto(
    val titulo: String,
    val contenido: String,
    val imagenUrl: String? = null,
    val tipoPublicacion: String
)
