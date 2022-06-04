package com.uberando.hipasar.data.source.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CartProductWithImage(
  @Embedded val product: CartProductEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "product_id"
  )
  val image: CartProductImageEntity?
)
