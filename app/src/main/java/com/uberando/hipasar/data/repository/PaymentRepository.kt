package com.uberando.hipasar.data.repository

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.entity.PaymentMethod
import com.uberando.hipasar.entity.request.PaymentRequest
import com.uberando.hipasar.entity.response.RequestPaymentResponseWrapper

interface PaymentRepository {
  suspend fun requestPayment(request: PaymentRequest): Resource<RequestPaymentResponseWrapper>
  suspend fun getPaymentType(): Resource<List<String>>
  suspend fun getPaymentWalletType(): Resource<List<String>>
  suspend fun getPaymentBankCode(): Resource<List<String>>
  suspend fun getPaymentMethodWithIcon(): Resource<List<PaymentMethod>>
}