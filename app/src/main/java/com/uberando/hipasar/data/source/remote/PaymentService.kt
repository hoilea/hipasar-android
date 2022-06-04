package com.uberando.hipasar.data.source.remote

import com.uberando.hipasar.entity.PaymentMethod
import com.uberando.hipasar.entity.request.PaymentRequest
import com.uberando.hipasar.entity.response.RequestPaymentResponseWrapper
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface PaymentService {

  @POST("payment/pay")
  suspend fun requestPayment(
    @Header("Authorization") token: String,
    @Body request: PaymentRequest
  ): RequestPaymentResponseWrapper

  @GET("payment/type")
  suspend fun getPaymentType(): List<String>

  @GET("payment/wallet/type")
  suspend fun getPaymentWalletType(): List<String>

  @GET("payment/bank/code")
  suspend fun getPaymentBankCode(): List<String>

  @GET("paymenticon")
  suspend fun getPaymentMethodWithIcon(): List<PaymentMethod>

}