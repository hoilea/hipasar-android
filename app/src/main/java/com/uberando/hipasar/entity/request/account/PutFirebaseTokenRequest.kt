package com.uberando.hipasar.entity.request.account

import com.google.gson.annotations.SerializedName

data class PutFirebaseTokenRequest(
  @SerializedName("firebase_token") val firebaseToken: String
)