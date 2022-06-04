package com.uberando.hipasar.entity.request

import com.google.gson.annotations.SerializedName

data class SignInOAuthRequest(
  @SerializedName("id") val id: String,
  @SerializedName("name") val name: String,
  @SerializedName("email") val email: String,
  /** both 'google' or 'kakao' */
  @SerializedName("account_type") val accountType: String
)
