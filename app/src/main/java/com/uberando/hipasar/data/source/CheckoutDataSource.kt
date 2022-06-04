package com.uberando.hipasar.data.source

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.CheckoutRepository
import com.uberando.hipasar.data.source.local.dao.AccountDao
import com.uberando.hipasar.data.source.remote.CheckoutService
import com.uberando.hipasar.entity.Address
import com.uberando.hipasar.entity.DeliveryTime
import com.uberando.hipasar.entity.Fee
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.DateUtil
import com.uberando.hipasar.util.makeBearerToken
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

class CheckoutDataSource @Inject constructor(
  private val checkoutService: CheckoutService,
  private val accountDao: AccountDao
) : CheckoutRepository {
  
  override suspend fun getAddresses(): Resource<List<Address>> {
    return try {
       getTokenOrNull().let { token ->
         if (token != null) {
           checkoutService.getAddresses(token).let { response ->
             Resource.Success(response)
           }
         } else {
           Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
         }
       }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }
  
  override suspend fun getFees(): Resource<Fee> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          checkoutService.getFees(token).let { response ->
            Resource.Success(response)
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun getDeliveryTime(): Resource<List<DeliveryTime>> {
    return try {
      checkoutService.getDeliveryTime().let { response ->
        Resource.Success(response)
      }
    } catch (e: Exception) {
      Resource.Failed(message = e.message.toString())
    }
  }

  override suspend fun getDeliveryDate(): Resource<List<String>> {
    return try {
      Resource.Success(DateUtil.getDeliveryDateInterval(Date(), DELIVERY_DATE_INTERVAL))
    } catch (e: Exception) {
      Resource.Failed(message = e.message.toString())
    }
  }

  private suspend fun getTokenOrNull(): String? = accountDao.getSingleAccount()?.token?.makeBearerToken()

  companion object {
    private const val DELIVERY_DATE_INTERVAL = 3
  }

}