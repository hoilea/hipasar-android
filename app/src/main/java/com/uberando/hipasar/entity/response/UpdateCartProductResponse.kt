package com.uberando.hipasar.entity.response

import com.google.gson.annotations.SerializedName

data class UpdateCartProductResponse(
  @SerializedName("product_id") val productId: Int,
  @SerializedName("name") val name: String,
  @SerializedName("image_id") val imageId: String,
  @SerializedName("unit_per_gram") val unitPerGram: Int,
  @SerializedName("price") val price: Int,
  @SerializedName("final_price") val finalPrice: Int,
  @SerializedName("qty") val quantity: Int,
)