package com.hugodev.red_up.features.auth.data.datasources.remote.models

import com.google.gson.annotations.SerializedName

data class AuthLoginRequestDto(
    @SerializedName("correo_institucional") val correoInstitucional: String,
    @SerializedName("password") val password: String
)

data class ForgotPasswordRequestDto(
    @SerializedName("correo_institucional") val correoInstitucional: String
)

data class ForgotPasswordConfirmRequestDto(
    @SerializedName("correo_institucional") val correoInstitucional: String,
    @SerializedName("codigo") val codigo: String,
    @SerializedName("nueva_password") val nuevaPassword: String
)

data class ForgotPasswordResponseDto(
    @SerializedName("message") val message: String,
    @SerializedName("reset_code") val resetCode: String? = null
)

data class AuthRegisterRequestDto(
    @SerializedName("correo_institucional") val correoInstitucional: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido_paterno") val apellidoPaterno: String,
    @SerializedName("apellido_materno") val apellidoMaterno: String? = null,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("foto_perfil_url") val fotoPerfilUrl: String? = null,
    @SerializedName("biografia") val biografia: String? = null,
    @SerializedName("carrera_id") val carreraId: Long? = null,
    @SerializedName("cuatrimestre_id") val cuatrimestreId: Long? = null,
    @SerializedName("password") val password: String
)

data class AuthUserDto(
    @SerializedName("id") val id: Long,
    @SerializedName("correo_institucional") val correoInstitucional: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellido_paterno") val apellidoPaterno: String,
    @SerializedName("apellido_materno") val apellidoMaterno: String? = null,
    @SerializedName("foto_perfil_url") val fotoPerfilUrl: String? = null,
    @SerializedName("rol") val rol: String? = null
)

data class AuthTokenDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("usuario") val usuario: AuthUserDto
)
