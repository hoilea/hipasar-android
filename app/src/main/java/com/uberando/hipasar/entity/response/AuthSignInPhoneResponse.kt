package com.uberando.hipasar.entity.response

import com.google.gson.annotations.SerializedName

data class AuthSignInPhoneResponse(
  @SerializedName("phone_verified") val phoneVerified: Boolean,
  @SerializedName("token") val token: String
)
