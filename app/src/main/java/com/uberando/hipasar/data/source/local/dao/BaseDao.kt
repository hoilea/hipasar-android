package com.uberando.hipasar.data.source.local.dao

import androidx.room.Delete
import androidx.room.Insert

interface BaseDao<T> {

  @Insert
  suspend fun insert(obj: T)

  @Delete
  suspend fun delete(obj: T)

}