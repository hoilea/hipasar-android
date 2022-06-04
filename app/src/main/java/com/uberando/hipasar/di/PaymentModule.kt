package com.uberando.hipasar.di

import com.uberando.hipasar.data.repository.PaymentRepository
import com.uberando.hipasar.data.source.PaymentDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class PaymentModule {
  @Binds
  abstract fun bindPaymentRepository(source: PaymentDataSource): PaymentRepository
}