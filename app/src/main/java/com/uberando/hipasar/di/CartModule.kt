package com.uberando.hipasar.di

import com.uberando.hipasar.data.repository.CartRepository
import com.uberando.hipasar.data.source.CartDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class CartModule {
  @Binds
  abstract fun bindCartRepository(source: CartDataSource): CartRepository
}