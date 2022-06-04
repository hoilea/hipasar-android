package com.uberando.hipasar.di

import com.uberando.hipasar.data.repository.BannerRepository
import com.uberando.hipasar.data.source.BannerDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
abstract class BannerModule {
  @Binds
  abstract fun bindBannerRepository(source: BannerDataSource) : BannerRepository
}