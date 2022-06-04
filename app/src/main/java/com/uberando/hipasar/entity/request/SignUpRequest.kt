package com.uberando.hipasar.entity.request

data class SignUpRequest(
  val name: String,
  val phone: String,
  val email: String?,
  val password: String
)