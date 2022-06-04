package com.uberando.hipasar.entity.request.address

import com.google.gson.annotations.SerializedName

class CreateAddressRequest(
  @SerializedName("basic_address") val basicAddress: String,
  @SerializedName("detail_address") val detailAddress: String,
  @SerializedName("post_code") val postCode: String = "",
  @SerializedName("reference") val reference: String = "",
  @SerializedName("phone") val phone: String,
  @SerializedName("receiver_name") val receiverName: String,
  @SerializedName("default") var primary: Boolean = false
)
