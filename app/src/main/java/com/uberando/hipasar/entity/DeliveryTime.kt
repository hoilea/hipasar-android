package com.uberando.hipasar.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeliveryTime(
  @SerializedName("id") val id: Int = 0,
  @SerializedName("name") val name: String = "",
  @SerializedName("name_en") val nameEn: String = "",
  @SerializedName("s_time") val sTime: String = "",
  @SerializedName("e_time") val eTime: String = "",
  @SerializedName("status") val status: Int? = null
): Parcelable