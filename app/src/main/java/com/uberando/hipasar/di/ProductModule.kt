package com.uberando.hipasar.di

import com.uberando.hipasar.data.repository.ProductRepository
import com.uberando.hipasar.data.source.ProductDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
abstract class ProductModule {
  @Binds
  abstract fun bindProductRepository(source: ProductDataSource): ProductRepository
}