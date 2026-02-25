package com.hugodev.red_up.features.auth.domain.usecases

import com.hugodev.red_up.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.logout()
    }
}
