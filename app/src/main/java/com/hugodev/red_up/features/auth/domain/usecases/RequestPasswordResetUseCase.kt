package com.hugodev.red_up.features.auth.domain.usecases

import com.hugodev.red_up.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class RequestPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<String?> {
        return authRepository.requestPasswordReset(email)
    }
}
