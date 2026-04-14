package com.hugodev.red_up.features.auth.data.repositories

import com.hugodev.red_up.core.data.AuthPreferences
import com.hugodev.red_up.features.auth.data.datasources.remote.api.AuthApi
import com.hugodev.red_up.features.auth.data.datasources.remote.models.AuthLoginRequestDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.ForgotPasswordConfirmRequestDto
import com.hugodev.red_up.features.auth.data.datasources.remote.models.ForgotPasswordRequestDto
import com.hugodev.red_up.features.auth.domain.entities.AuthUser
import com.hugodev.red_up.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

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
        fotoPerfil: MultipartBody.Part?,
        password: String
    ): Result<AuthUser> {
        return runCatching {
            val user = authApi.register(
                correoInstitucional = email.toRequestBody("text/plain".toMediaType()),
                nombre = nombre.toRequestBody("text/plain".toMediaType()),
                apellidoPaterno = apellidoPaterno.toRequestBody("text/plain".toMediaType()),
                apellidoMaterno = apellidoMaterno?.toRequestBody("text/plain".toMediaType()),
                fechaNacimiento = fechaNacimiento.toRequestBody("text/plain".toMediaType()),
                password = password.toRequestBody("text/plain".toMediaType()),
                fotoPerfil = fotoPerfil
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
