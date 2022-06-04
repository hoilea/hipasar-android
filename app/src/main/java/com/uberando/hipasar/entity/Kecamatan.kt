package com.uberando.hipasar.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kecamatan(
  @SerializedName("id") val id: Long = 0,
  @SerializedName("id_kota") val cityId: String = "",
  @SerializedName("nama") val name: String = ""
): Parcelable
