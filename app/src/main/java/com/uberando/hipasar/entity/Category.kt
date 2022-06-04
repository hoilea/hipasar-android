package com.uberando.hipasar.entity

import com.google.gson.annotations.SerializedName

data class Category(
  @SerializedName("id") val id: Int,
  @SerializedName("name") val name: String,
  @SerializedName("name_en") val nameEn: String,
  @SerializedName("description") val description: String,
  @SerializedName("description_en") val descriptionEn: String,
  @SerializedName("status") val status: Int?,
  @SerializedName("products") var products: List<Product>
)
