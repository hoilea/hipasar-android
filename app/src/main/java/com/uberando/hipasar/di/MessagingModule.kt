package com.uberando.hipasar.di

import com.uberando.hipasar.data.repository.MessagingRepository
import com.uberando.hipasar.data.source.MessagingDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MessagingModule {
  @Binds
  abstract fun bindMessagingRepository(source: MessagingDataSource): MessagingRepository
}