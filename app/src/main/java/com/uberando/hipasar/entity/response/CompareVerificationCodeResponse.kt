package com.uberando.hipasar.entity.response

data class CompareVerificationCodeResponse(
  val code: Int,
  val token: String?
)
