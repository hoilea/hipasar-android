package com.uberando.hipasar.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
  @SerializedName("id") val id: String,
  @SerializedName("name") val name: String? = "",
  @SerializedName("email") val email: String? = "",
  @SerializedName("phone") val phone: String? = "",
  @SerializedName("phoneVerified") val phoneVerified: Boolean? = false,
  @SerializedName("image") var image: Image? = null,
  @SerializedName("password_available") val passwordAvailable: Boolean? = false
): Parcelable
