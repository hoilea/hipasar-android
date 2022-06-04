package com.uberando.hipasar.entity.request

import com.google.gson.annotations.SerializedName

data class CompareVerificationCodeRequest(
  @SerializedName("verification_code") val verificationCode: String
)
