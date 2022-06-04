package com.uberando.hipasar.entity

import com.google.gson.annotations.SerializedName

data class OrderProduct(
  @SerializedName("id") val id: Int,
  @SerializedName("name") val name: String,
  @SerializedName("image") val image: Image?,
  @SerializedName("unit_per_gram") val unitPerGram: Int,
  @SerializedName("qty") val qty: Int,
  @SerializedName("minimum_quantity") val minimumQuantity: Int = 1,
  @SerializedName("price") val price: Int,
  @SerializedName("final_price") val finalPrice: Int
)