package com.uberando.hipasar.entity

import com.google.gson.annotations.SerializedName

data class Product(
  @SerializedName("id") val id: Int,
  @SerializedName("name") val name: String,
  @SerializedName("name_en") val nameEn: String,
  @SerializedName("description") val description: String,
  @SerializedName("description_en") val descriptionEn: String,
  @SerializedName("price") val price: Int,
  @SerializedName("unit_per_gram") val unitPerGram: Int,
  @SerializedName("minimum_order") val minimumQuantity: Int,
  @SerializedName("stock") val stock: Int,
  @SerializedName("created_at") val createdAt: String? = null,
  @SerializedName("updated_at") val updatedAt: String? = null,
  @SerializedName("deleted_at") val deletedAt: String? = null,
  @SerializedName("image") var image: Image?,
  var productOnCart: Boolean = false,
  var productOnCartAmount: Int = 0,

  /** Detail only */
  @SerializedName("images") val images: List<Image> = emptyList(),
  @SerializedName("categories") val categories: List<Category> = emptyList(),
) {
  fun requireMinimumQuantity() = minimumQuantity.toString()
  fun requireStock() = stock.toString()
}