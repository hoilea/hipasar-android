package com.uberando.hipasar.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.uberando.hipasar.data.source.local.entity.CartProductEntity
import com.uberando.hipasar.data.source.local.entity.CartProductWithImage

@Dao
interface CartProductDao : BaseDao<CartProductEntity> {

  @Transaction
  @Query("SELECT * FROM cart_product")
  suspend fun getCartProductWithImage(): List<CartProductWithImage>

  @Query("UPDATE cart_product SET amount = :amount WHERE id = :productId")
  suspend fun updateCartProductAmount(productId: Int, amount: Int)

  @Query("DELETE FROM cart_product WHERE id = :productId")
  suspend fun deleteCartProductById(productId: Int)

  @Query("DELETE FROM cart_product")
  suspend fun deleteAllCartProducts()

}