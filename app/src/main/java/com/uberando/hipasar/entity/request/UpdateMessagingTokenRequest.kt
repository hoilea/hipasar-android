package com.uberando.hipasar.entity.request

import com.google.gson.annotations.SerializedName

data class UpdateMessagingTokenRequest(
  @SerializedName("firebase_token") val firebaseToken: String
)