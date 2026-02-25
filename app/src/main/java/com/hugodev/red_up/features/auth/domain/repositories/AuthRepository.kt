package com.hugodev.red_up.features.auth.domain.repositories

import com.hugodev.red_up.features.auth.domain.entities.AuthUser

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthUser>
    suspend fun register(
        email: String,
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String?,
        fechaNacimiento: String,
        password: String
    ): Result<AuthUser>

    suspend fun logout()
}
