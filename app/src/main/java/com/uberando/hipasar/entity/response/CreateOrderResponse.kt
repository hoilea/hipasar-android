package com.uberando.hipasar.entity.response

import com.google.gson.annotations.SerializedName

data class CreateOrderResponse(
  @SerializedName("id") val id: String,
  @SerializedName("order_code") val orderCode: String
)
