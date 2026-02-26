package com.hugodev.red_up.features.chat.domain.usecases

import com.hugodev.red_up.features.chat.domain.entities.ChatMessage
import com.hugodev.red_up.features.chat.domain.repositories.ChatRepository
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(message: ChatMessage) {
        repository.sendMessage(message)
    }
}
