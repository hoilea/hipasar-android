package com.uberando.hipasar.di

import com.uberando.hipasar.data.repository.AddressRepository
import com.uberando.hipasar.data.source.AddressDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
abstract class AddressModule {
  @Binds
  abstract fun bindAddressRepository(source: AddressDataSource): AddressRepository
}