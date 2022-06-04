package com.uberando.hipasar.data.source

import com.uberando.hipasar.data.repository.MessagingRepository
import com.uberando.hipasar.data.source.local.dao.AccountDao
import com.uberando.hipasar.data.source.remote.MessagingService
import com.uberando.hipasar.entity.request.UpdateMessagingTokenRequest
import com.uberando.hipasar.util.makeBearerToken
import timber.log.Timber
import javax.inject.Inject

class MessagingDataSource @Inject constructor(
  private val accountDao: AccountDao,
  private val messagingService: MessagingService
) : MessagingRepository {

  override suspend fun updateMessagingToken(firebaseToken: String) {
    try {
      getTokenOrNull().let { token ->
        if (token != null) {
          messagingService.updateMessagingToken(token, UpdateMessagingTokenRequest(firebaseToken))
        } else {
          Timber.i("user unauthorized")
        }
      }
    } catch (e: Exception) {
      Timber.e(e)
    }
  }

  private suspend fun getTokenOrNull(): String? = accountDao.getSingleAccount()?.token?.makeBearerToken()

}