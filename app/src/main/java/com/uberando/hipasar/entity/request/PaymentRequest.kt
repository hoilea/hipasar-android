package com.uberando.hipasar.entity.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentRequest(
  @SerializedName("type") val type: String = "",
  @SerializedName("order_id") var orderId: String = "",
  @SerializedName("bankCode") val bankCode: String = "",
  @SerializedName("an") var accountName: String = "",
  @SerializedName("mobileNumber") var mobileNumber: String = "",
  @SerializedName("walletType") val walletType: String = ""
): Parcelable