package com.hugodev.red_up.features.chat.data.di

import com.hugodev.red_up.features.chat.data.repositories.SocketIoChatRepository
import com.hugodev.red_up.features.chat.domain.repositories.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatModule {
    @Binds
    @Singleton
    abstract fun bindChatRepository(
        socketIoChatRepository: SocketIoChatRepository
    ): ChatRepository
}
