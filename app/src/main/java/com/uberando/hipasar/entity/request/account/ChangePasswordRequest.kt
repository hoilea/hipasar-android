package com.uberando.hipasar.entity.request.account

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
  @SerializedName("password") val password: String,
  @SerializedName("new_password") val newPassword: String
)
