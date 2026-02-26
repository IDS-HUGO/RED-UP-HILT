package com.hugodev.red_up.features.chat.domain.usecases

import com.hugodev.red_up.features.chat.domain.repositories.ChatRepository
import javax.inject.Inject

class DisconnectFromChatUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke() {
        repository.disconnect()
    }
}
