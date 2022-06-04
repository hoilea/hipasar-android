package com.uberando.hipasar.data.repository

interface MessagingRepository {
  suspend fun updateMessagingToken(firebaseToken: String)
}