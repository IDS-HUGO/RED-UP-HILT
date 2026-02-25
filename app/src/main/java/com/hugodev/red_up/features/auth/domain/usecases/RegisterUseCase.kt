package com.hugodev.red_up.features.auth.domain.usecases

import com.hugodev.red_up.features.auth.domain.entities.AuthUser
import com.hugodev.red_up.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String?,
        fechaNacimiento: String,
        password: String
    ): Result<AuthUser> {
        return authRepository.register(
            email = email,
            nombre = nombre,
            apellidoPaterno = apellidoPaterno,
            apellidoMaterno = apellidoMaterno,
            fechaNacimiento = fechaNacimiento,
            password = password
        )
    }
}
