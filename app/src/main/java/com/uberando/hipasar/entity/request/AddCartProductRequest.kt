package com.uberando.hipasar.entity.request

import com.google.gson.annotations.SerializedName

data class AddCartProductRequest(
  @SerializedName("product_id") val productId: Int,
  @SerializedName("name") val name: String,
  @SerializedName("image_id") val imageId: String,
  @SerializedName("unit_per_gram") val unitPerGram: Int,
  @SerializedName("price") val price: Int
)
