package com.hugodev.red_up.features.chat.domain.usecases

import com.hugodev.red_up.features.chat.domain.repositories.ChatRepository
import javax.inject.Inject

class JoinDirectChatUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(otherUserId: String) {
        repository.joinDirectChat(otherUserId)
    }
}