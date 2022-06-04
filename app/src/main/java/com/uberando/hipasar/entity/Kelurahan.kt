package com.uberando.hipasar.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kelurahan(
  @SerializedName("id") val id: Long,
  @SerializedName("id_kecamatan") val kecamatanId: String,
  @SerializedName("nama") val name: String
): Parcelable
