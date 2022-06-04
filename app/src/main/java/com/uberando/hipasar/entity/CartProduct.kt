package com.uberando.hipasar.entity

import com.google.gson.annotations.SerializedName

data class CartProduct(
  @SerializedName("product_id") val id: Int,
  @SerializedName("name") val name: String,
  @SerializedName("qty") var quantity: Int,
  @SerializedName("unit_per_gram") val unitPerGram: Int,
  @SerializedName("price") val price: Int,
  @SerializedName("final_price") val finalPrice: Int,
  @SerializedName("image") val image: Image
)
