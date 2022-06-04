package com.uberando.hipasar.entity.request.order

import com.google.gson.annotations.SerializedName

data class OrderProductRequest(
  @SerializedName("product_id") val id: Int,
  @SerializedName("name") val name: String,
  @SerializedName("image_id") val imageId: String?,
  @SerializedName("qty") val quantity: Int,
  @SerializedName("minimum_order") val minimumQuantity: Int,
  @SerializedName("price") val price: Int,
  @SerializedName("unit_per_gram") val unitPerGram: Int,
  @SerializedName("final_price") val finalPrice: Int
)
