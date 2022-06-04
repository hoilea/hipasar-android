package com.uberando.hipasar.di

import com.uberando.hipasar.data.repository.AuthRepository
import com.uberando.hipasar.data.source.AuthDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class AuthModule {
  @Binds
  abstract fun bindAuthRepository(source: AuthDataSource): AuthRepository
}