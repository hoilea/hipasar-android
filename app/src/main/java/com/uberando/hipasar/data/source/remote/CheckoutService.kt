package com.uberando.hipasar.data.source.remote

import com.uberando.hipasar.entity.Address
import com.uberando.hipasar.entity.DeliveryTime
import com.uberando.hipasar.entity.Fee
import retrofit2.http.GET
import retrofit2.http.Header

interface CheckoutService {
  
  @GET("checkout/addresses")
  suspend fun getAddresses(
    @Header("Authorization") token: String
  ): List<Address>
  
  @GET("checkout/fees")
  suspend fun getFees(
    @Header("Authorization") token: String
  ): Fee

  @GET("deliveryTime")
  suspend fun getDeliveryTime(): List<DeliveryTime>
  
}