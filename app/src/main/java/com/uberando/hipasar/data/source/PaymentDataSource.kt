package com.uberando.hipasar.data.source

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.PaymentRepository
import com.uberando.hipasar.data.source.local.dao.AccountDao
import com.uberando.hipasar.data.source.remote.PaymentService
import com.uberando.hipasar.entity.PaymentMethod
import com.uberando.hipasar.entity.request.PaymentRequest
import com.uberando.hipasar.entity.response.RequestPaymentResponseWrapper
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.activeOnly
import com.uberando.hipasar.util.buildPaymentUrl
import com.uberando.hipasar.util.makeBearerToken
import javax.inject.Inject

class PaymentDataSource @Inject constructor(
  private val paymentService: PaymentService,
  private val accountDao: AccountDao
) : PaymentRepository {

  override suspend fun requestPayment(request: PaymentRequest): Resource<RequestPaymentResponseWrapper> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          paymentService.requestPayment(token, request).let { response ->
            Resource.Success(response.buildPaymentUrl(request.walletType))
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed(message = e.message.toString())
    }
  }

  override suspend fun getPaymentType(): Resource<List<String>> {
     return try {
      paymentService.getPaymentType().let { response ->
        Resource.Success(response)
      }
    } catch (e: Exception) {
      Resource.Failed(message = e.message.toString())
    }
  }

  override suspend fun getPaymentWalletType(): Resource<List<String>> {
     return try {
      paymentService.getPaymentWalletType().let { response ->
        Resource.Success(response)
      }
    } catch (e: Exception) {
      Resource.Failed(message = e.message.toString())
    }
  }

  override suspend fun getPaymentBankCode(): Resource<List<String>> {
     return try {
      paymentService.getPaymentBankCode().let { response ->
        Resource.Success(response)
      }
    } catch (e: Exception) {
      Resource.Failed(message = e.message.toString())
    }
  }

  override suspend fun getPaymentMethodWithIcon(): Resource<List<PaymentMethod>> {
    return try {
      paymentService.getPaymentMethodWithIcon().let { response ->
        Resource.Success(response.activeOnly())
      }
    } catch (e: Exception) {
      Resource.Failed(message = e.message.toString())
    }
  }

  private suspend fun getTokenOrNull(): String? = accountDao.getSingleAccount()?.token?.makeBearerToken()

}