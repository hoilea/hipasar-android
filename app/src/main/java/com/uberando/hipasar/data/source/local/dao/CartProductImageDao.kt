package com.uberando.hipasar.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.uberando.hipasar.data.source.local.entity.CartProductImageEntity

@Dao
interface CartProductImageDao : BaseDao<CartProductImageEntity> {

  @Query("DELETE FROM cart_product_image WHERE product_id = :productId")
  suspend fun deleteByProductId(productId: Int)

  @Query("DELETE FROM cart_product_image")
  suspend fun deleteAllCartProductImages()

}