package com.uberando.hipasar.entity.request

import com.google.gson.annotations.SerializedName

data class UpdateCartQuantityRequest(
  @SerializedName("qty") val quantity: Int
)
