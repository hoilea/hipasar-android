package com.uberando.hipasar.data.source.remote

import com.uberando.hipasar.entity.Order
import com.uberando.hipasar.entity.request.order.CreateOrderRequest
import com.uberando.hipasar.entity.response.CreateOrderResponse
import com.uberando.hipasar.entity.response.SuccessStateResponse
import retrofit2.http.*

interface OrderService {

  @GET("orders/mine")
  suspend fun getOrders(
    @Header("Authorization") token: String
  ): List<Order>

  @GET("orders/{id}/details")
  suspend fun getOrder(
    @Header("Authorization") token: String,
    @Path("id") orderId: String
  ): Order

  @POST("orders")
  suspend fun createOrder(
    @Header("Authorization") token: String,
    @Body request: CreateOrderRequest
  ): CreateOrderResponse

  @PUT("orders/{id}/cancel")
  suspend fun cancelOrder(
    @Header("Authorization") token: String,
    @Path("id") orderId: String
  ): SuccessStateResponse

  @PUT("orders/{id}/confirm/arrived")
  suspend fun confirmOrderArrived(
    @Header("Authorization") token: String,
    @Path("id") orderId: String
  ): SuccessStateResponse

}