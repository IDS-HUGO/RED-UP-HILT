package com.hugodev.red_up.features.auth.data.repositories

import com.hugodev.red_up.core.data.AuthPreferences
import com.hugodev.red_up.features.auth.data.datasources.remote.api.AuthApi
import com.hugodev.red_up.features.auth.data.datasources.remote.models.ForgotPasswordConfirmRequestDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.ForgotPasswordRequestDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.AuthLoginRequestDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.AuthRegisterRequestDto
import com.hugodev.red_up.features.auth.domain.entities.AuthUser
import com.hugodev.red_up.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val authPreferences: AuthPreferences
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthUser> {
        return runCatching {
            val tokenResponse = authApi.login(
                AuthLoginRequestDto(
                    correoInstitucional = email,
                    password = password
                )
            )

            authPreferences.saveToken(tokenResponse.accessToken)
            authPreferences.saveUser(
                id = tokenResponse.usuario.id,
                name = "${tokenResponse.usuario.nombre} ${tokenResponse.usuario.apellidoPaterno}"
            )

            tokenResponse.usuario.toDomain()
        }
    }

    override suspend fun register(
        email: String,
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String?,
        fechaNacimiento: String,
        fotoUrl: String?,
        password: String
    ): Result<AuthUser> {
        return runCatching {
            val user = authApi.register(
                AuthRegisterRequestDto(
                    correoInstitucional = email,
                    nombre = nombre,
                    apellidoPaterno = apellidoPaterno,
                    apellidoMaterno = apellidoMaterno,
                    fechaNacimiento = fechaNacimiento,
                    fotoPerfilUrl = fotoUrl,
                    password = password
                )
            )
            user.toDomain()
        }
    }

    override suspend fun requestPasswordReset(email: String): Result<String?> {
        return runCatching {
            val response = authApi.requestPasswordReset(
                ForgotPasswordRequestDto(correoInstitucional = email)
            )
            response.resetCode
        }
    }

    override suspend fun confirmPasswordReset(email: String, codigo: String, nuevaPassword: String): Result<Unit> {
        return runCatching {
            authApi.confirmPasswordReset(
                ForgotPasswordConfirmRequestDto(
                    correoInstitucional = email,
                    codigo = codigo,
                    nuevaPassword = nuevaPassword
                )
            )
            Unit
        }
    }

    override suspend fun logout() {
        authPreferences.clear()
    }

    private fun com.hugodev.red_up.features.auth.data.datasources.remote.models.AuthUserDto.toDomain(): AuthUser {
        val fullName = listOfNotNull(nombre, apellidoPaterno, apellidoMaterno).joinToString(" ")
        return AuthUser(
            id = id,
            email = correoInstitucional,
            fullName = fullName.trim(),
            avatarUrl = fotoPerfilUrl
        )
    }
}
