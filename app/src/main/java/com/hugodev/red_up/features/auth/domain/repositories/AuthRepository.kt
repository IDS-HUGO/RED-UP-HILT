package com.hugodev.red_up.features.auth.domain.repositories

import com.hugodev.red_up.features.auth.domain.entities.AuthUser
import okhttp3.MultipartBody

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthUser>
    suspend fun requestPasswordReset(email: String): Result<String?>
    suspend fun confirmPasswordReset(email: String, codigo: String, nuevaPassword: String): Result<Unit>
    suspend fun register(
        email: String,
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String?,
        fechaNacimiento: String,
        fotoPerfil: MultipartBody.Part?,
        password: String
    ): Result<AuthUser>

    suspend fun logout()
}
