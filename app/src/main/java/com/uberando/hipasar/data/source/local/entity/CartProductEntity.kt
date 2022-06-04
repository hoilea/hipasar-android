package com.uberando.hipasar.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_product")
data class CartProductEntity(
  @PrimaryKey(autoGenerate = false) val id: Int,
  @ColumnInfo(name = "name") val name: String,
  @ColumnInfo(name = "name_en") val nameEn: String,
  @ColumnInfo(name = "description") val description: String,
  @ColumnInfo(name = "description_en") val descriptionEn: String,
  @ColumnInfo(name = "price") val price: Int,
  @ColumnInfo(name = "unit_per_gram") val unitPerGram: Int,
  @ColumnInfo(name = "minimum_quantity") val minimumQuantity: Int,
  @ColumnInfo(name = "created_at") val createdAt: String?,
  @ColumnInfo(name = "updated_at") val updatedAt: String?,
  @ColumnInfo(name = "deleted_at") val deletedAt: String?,
  @ColumnInfo(name = "amount") val amount: Int
)
