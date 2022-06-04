package com.uberando.hipasar.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Banner(
  @SerializedName("id") val id: Int,
  @SerializedName("category_id") val categoryId: Int?,
  @SerializedName("image_id") val imageId: String,
  @SerializedName("is_active") val isActive: Boolean,
  @SerializedName("label") val label: String,
  @SerializedName("image") val image: Image
): Parcelable
