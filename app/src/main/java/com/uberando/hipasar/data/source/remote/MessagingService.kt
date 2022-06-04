package com.uberando.hipasar.data.source.remote

import com.uberando.hipasar.entity.request.UpdateMessagingTokenRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT

interface MessagingService {

  @PUT("users/firebase/token")
  suspend fun updateMessagingToken(
    @Header("Authorization") token: String,
    @Body request: UpdateMessagingTokenRequest
  )

}