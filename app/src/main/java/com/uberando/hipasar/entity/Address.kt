package com.uberando.hipasar.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
  @SerializedName("id") val id: Int = 0,
  @SerializedName("basic_address") var basicAddress: String = "",
  @SerializedName("detail_address") var detailAddress: String = "",
  @SerializedName("post_code") var postCode: String = "",
  @SerializedName("reference") var reference: String = "",
  @SerializedName("phone") var phone: String = "",
  @SerializedName("receiver_name") var receiverName: String = "",
  @SerializedName("default") var primary: Boolean = false,
  
  /** to indicate selected address on checkout address selection */
  var selectedAddress: Boolean = false
): Parcelable