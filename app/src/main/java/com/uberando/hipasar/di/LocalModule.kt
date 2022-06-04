package com.uberando.hipasar.di

import android.content.Context
import androidx.room.Room
import com.uberando.hipasar.data.source.local.AppDatabase
import com.uberando.hipasar.data.source.local.dao.AccountDao
import com.uberando.hipasar.data.source.local.dao.CartProductDao
import com.uberando.hipasar.data.source.local.dao.CartProductImageDao
import com.uberando.hipasar.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

  @Provides
  @Singleton
  fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
    Room.databaseBuilder(
      context,
      AppDatabase::class.java,
      Constants.DATABASE_NAME
    )
      .fallbackToDestructiveMigration()
      .build()

  @Provides
  fun provideAccountDao(appDatabase: AppDatabase): AccountDao =
    appDatabase.accountDao()

  @Provides
  fun provideCartProductDao(appDatabase: AppDatabase): CartProductDao =
    appDatabase.cartProductDao()

  @Provides
  fun provideCartProductImageDao(appDatabase: AppDatabase): CartProductImageDao =
    appDatabase.cartProductImageDao()

}