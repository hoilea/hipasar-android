package com.uberando.hipasar.entity.response

import com.google.gson.annotations.SerializedName

data class RequestPaymentResponse(
  @SerializedName("payment_id") val paymentId: String = "",
  @SerializedName("order_id") val orderId: String = "",
  @SerializedName("expiration_time") val expirationTime: String = "",
  @SerializedName("paid_amount") val paidAmount: String = "",

  /**
   * e-wallet only
   */
  @SerializedName("mobile") val mobile: String = "",
  @SerializedName("status") val status: String = "",
  @SerializedName("checkout_url") val checkoutUrl: String = "",
  @SerializedName("web_url") val webUrl: String = "",
  @SerializedName("unique_id") val uniqueId: String = "",
  /**
   * Payment url for every wallet, maybe null because some e-wallet doesn't require
   * payment url. And it's difference for every e-wallet type
   */
  var paymentUrl: String? = null,

  /**
   * virtual account only
   */
  @SerializedName("account_number") val accountNumber: String = "",
)
