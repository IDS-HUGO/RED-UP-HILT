package com.hugodev.red_up.features.chat.domain.usecases

import com.hugodev.red_up.features.chat.domain.repositories.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveChatConnectionUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.observeConnection()
    }
}
