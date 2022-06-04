package com.uberando.hipasar.entity.request.account

import com.google.gson.annotations.SerializedName

data class SetPasswordOnlyRequest(
  @SerializedName("new_password") val newPassword: String
)
