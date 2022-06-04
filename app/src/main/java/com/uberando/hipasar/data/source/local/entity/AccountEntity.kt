package com.uberando.hipasar.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account")
data class AccountEntity(
  @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "temp_id") val tempId: Int,
  @ColumnInfo(name = "token") val token: String,
  @ColumnInfo(name = "method") val method: String
)