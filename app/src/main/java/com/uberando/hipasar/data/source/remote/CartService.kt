package com.uberando.hipasar.data.source.remote

import com.uberando.hipasar.entity.CartProduct
import com.uberando.hipasar.entity.request.AddCartProductRequest
import com.uberando.hipasar.entity.request.UpdateCartQuantityRequest
import com.uberando.hipasar.entity.response.SuccessStateResponse
import com.uberando.hipasar.entity.response.UpdateCartProductResponse
import retrofit2.http.*

interface CartService {

  @GET("cart")
  suspend fun getCartProducts(
    @Header("Authorization") token: String
  ): List<CartProduct>

  @POST("cart")
  suspend fun addCartProduct(
    @Header("Authorization") token: String,
    @Body request: AddCartProductRequest
  ): UpdateCartProductResponse

  @PUT("cart/{product_id}")
  suspend fun updateQuantity(
    @Header("Authorization") token: String,
    @Path("product_id") productId: Int,
    @Body request: UpdateCartQuantityRequest
  ): UpdateCartProductResponse

  @DELETE("cart/{product_id}")
  suspend fun deleteCartProduct(
    @Header("Authorization") token: String,
    @Path("product_id") productId: Int
  ): SuccessStateResponse

}