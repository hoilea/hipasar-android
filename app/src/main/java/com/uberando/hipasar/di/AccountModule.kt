package com.uberando.hipasar.di

import com.uberando.hipasar.data.repository.AccountRepository
import com.uberando.hipasar.data.source.AccountDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class AccountModule {
  @Binds
  abstract fun bindAccountRepository(source: AccountDataSource): AccountRepository
}