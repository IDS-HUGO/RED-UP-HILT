package com.hugodev.red_up.features.auth.domain.usecases

import com.hugodev.red_up.features.auth.domain.entities.AuthUser
import com.hugodev.red_up.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthUser> {
        return authRepository.login(email, password)
    }
}
