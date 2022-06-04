package com.uberando.hipasar.entity

import com.google.gson.annotations.SerializedName

data class ProductFeedback(
  @SerializedName("id") val id: Int,
  @SerializedName("rating") val rating: Int,
  @SerializedName("comment") val comment: String,
  @SerializedName("updatable") val updatable: Boolean,
  @SerializedName("images") val images: List<Image>,
  @SerializedName("user") val user: User,
  @SerializedName("created_at") val createdAt: String,
  @SerializedName("updated_at") val updatedAt: String
)
