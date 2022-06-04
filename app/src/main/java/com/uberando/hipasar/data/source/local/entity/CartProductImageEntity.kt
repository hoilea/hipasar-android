package com.uberando.hipasar.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_product_image")
data class CartProductImageEntity(
  @PrimaryKey(autoGenerate = false) val id: String,
  @ColumnInfo(name = "product_id") val productId: Int,
  @ColumnInfo(name = "path") val path: String
)