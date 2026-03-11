package com.hugodev.red_up.features.publications.domain.entities

data class Publications(
    val id: Long,
    val autorId: Long,
    val titulo: String,
    val contenido: String,
    val imagenUrl: String? = null,
    val audiencia: String,
    val publicadaEn: String,
    val autorNombre: String = "",
    val autorApellido: String = "",
    val autorFotoUrl: String? = null,
    val totalReacciones: Int = 0,
    val totalComentarios: Int = 0
)