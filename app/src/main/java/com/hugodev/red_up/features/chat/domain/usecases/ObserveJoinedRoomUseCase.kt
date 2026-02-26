package com.hugodev.red_up.features.chat.domain.usecases

import com.hugodev.red_up.features.chat.domain.repositories.ChatRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveJoinedRoomUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(): Flow<String> {
        return repository.observeJoinedRoom()
    }
}