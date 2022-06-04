package com.uberando.hipasar.entity

import com.google.gson.annotations.SerializedName

data class ProductSearch(
  @SerializedName("prod_id") val id: Int = 0,
  @SerializedName("prod_name") val name: String = "",
  @SerializedName("prod_description") val description: String = "",
  @SerializedName("unit_per_gram") val unitPerGram: Int = 0,
  @SerializedName("price") val price: Int = 0,
  @SerializedName("stock") val stock: Int = 0,
  @SerializedName("minimum_order") val minimumOrder: Int = 0,
  @SerializedName("image_id") val imageId: String = "",
  @SerializedName("path") val imagePath: String = ""
)
