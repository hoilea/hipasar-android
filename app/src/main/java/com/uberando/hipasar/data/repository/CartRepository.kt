package com.uberando.hipasar.data.repository

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.entity.Cart
import com.uberando.hipasar.entity.CartProduct
import com.uberando.hipasar.entity.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
  suspend fun getCart(): Resource<Cart>
  suspend fun insertProduct(product: Product): Resource<Nothing>
  suspend fun deleteProduct(productId: Int): Resource<Nothing>
  suspend fun updateProductAmount(productId: Int, amount: Int): Resource<Nothing>
  suspend fun clearCart(): Resource<Nothing>
}