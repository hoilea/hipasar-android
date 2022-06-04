package com.uberando.hipasar.entity.response

import com.google.gson.annotations.SerializedName

data class OAuthResponse(
  @SerializedName("token") val token: String,
  @SerializedName("password_available") val passwordAvailable: Boolean
)
