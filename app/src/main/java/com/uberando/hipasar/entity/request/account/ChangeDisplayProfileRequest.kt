package com.uberando.hipasar.entity.request.account

import com.google.gson.annotations.SerializedName

data class ChangeDisplayProfileRequest(
  @SerializedName("name") val name: String?,
  @SerializedName("image_id") val imageId: String?
)