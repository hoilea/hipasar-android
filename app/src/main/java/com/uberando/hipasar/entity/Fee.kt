package com.uberando.hipasar.entity

import com.google.gson.annotations.SerializedName

data class Fee(
  @SerializedName("tax_percentage") val taxPercentage: Int,
  @SerializedName("shipment_fee") val shipmentFee: Int
)
