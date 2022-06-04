package com.uberando.hipasar.di

import com.uberando.hipasar.data.repository.FileRepository
import com.uberando.hipasar.data.source.FileDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
abstract class FileModule {
  @Binds
  abstract fun bindFileRepository(source: FileDataSource): FileRepository
}