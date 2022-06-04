package com.uberando.hipasar.di

import com.uberando.hipasar.data.repository.CheckoutRepository
import com.uberando.hipasar.data.source.CheckoutDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
abstract class CheckoutModule {
  @Binds
  abstract fun bindCheckoutRepository(source: CheckoutDataSource): CheckoutRepository
}