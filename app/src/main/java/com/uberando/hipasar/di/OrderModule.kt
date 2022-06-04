package com.uberando.hipasar.di

import com.uberando.hipasar.data.repository.OrderRepository
import com.uberando.hipasar.data.source.OrderDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class OrderModule {
  @Binds
  abstract fun bindOrderRepository(source: OrderDataSource): OrderRepository
}