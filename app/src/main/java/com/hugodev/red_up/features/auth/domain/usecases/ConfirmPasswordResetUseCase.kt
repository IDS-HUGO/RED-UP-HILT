package com.hugodev.red_up.features.auth.domain.usecases

import com.hugodev.red_up.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class ConfirmPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, codigo: String, nuevaPassword: String): Result<Unit> {
        return authRepository.confirmPasswordReset(email, codigo, nuevaPassword)
    }
}
