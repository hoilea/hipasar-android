package com.uberando.hipasar.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.uberando.hipasar.data.source.local.dao.AccountDao
import com.uberando.hipasar.data.source.local.dao.CartProductDao
import com.uberando.hipasar.data.source.local.dao.CartProductImageDao
import com.uberando.hipasar.data.source.local.entity.AccountEntity
import com.uberando.hipasar.data.source.local.entity.CartProductEntity
import com.uberando.hipasar.data.source.local.entity.CartProductImageEntity

@Database(
  entities = [
    AccountEntity::class,
    CartProductEntity::class,
    CartProductImageEntity::class
  ],
  version = AppDatabase.DATABASE_VERSION,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun accountDao(): AccountDao
  abstract fun cartProductDao(): CartProductDao
  abstract fun cartProductImageDao(): CartProductImageDao
  companion object {
    const val DATABASE_VERSION = 2
  }
}