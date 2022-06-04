package com.uberando.hipasar.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMethod(
  @SerializedName("id") val id: Int?,
  @SerializedName("type") val type: String,
  @SerializedName("name") val name: String,
  @SerializedName("icon_path") val iconPath: String = "",
  @SerializedName("status") val status: Int = 0
): Parcelable
