package com.uberando.hipasar.entity.request

import com.google.gson.annotations.SerializedName

data class SetPasswordRequest(
  @SerializedName("email") val email: String,
  @SerializedName("new_password") val newPassword: String
)
