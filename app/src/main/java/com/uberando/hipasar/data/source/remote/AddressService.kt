package com.uberando.hipasar.data.source.remote

import com.uberando.hipasar.entity.Address
import com.uberando.hipasar.entity.request.address.CreateAddressRequest
import com.uberando.hipasar.entity.response.SuccessStateResponse
import retrofit2.http.*

interface AddressService {

  @GET("addresses")
  suspend fun getAddresses(
    @Header("Authorization") token: String
  ): List<Address>

  @GET("addresses/{id}")
  suspend fun getAddress(
    @Header("Authorization") token: String,
    @Path("id") addressId: Int
  ): Address

  @POST("addresses")
  suspend fun createAddress(
    @Header("Authorization") token: String,
    @Body request: CreateAddressRequest
  ): Address

  @PUT("addresses/{id}")
  suspend fun updateAddress(
    @Header("Authorization") token: String,
    @Path("id") addressId: Int,
    @Body request: CreateAddressRequest
  ): Address

  @DELETE("addresses/{id}")
  suspend fun removeAddress(
    @Header("Authorization") token: String,
    @Path("id") addressId: Int
  ): SuccessStateResponse

}