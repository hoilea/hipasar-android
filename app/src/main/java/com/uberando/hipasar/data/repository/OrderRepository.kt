package com.uberando.hipasar.data.repository

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.entity.Filter
import com.uberando.hipasar.entity.Order
import com.uberando.hipasar.entity.request.order.CreateOrderRequest
import com.uberando.hipasar.entity.response.CreateOrderResponse

interface OrderRepository {
  suspend fun getOrders(): Resource<List<Order>>
  suspend fun getOrdersByFilters(filterCodes: List<Int>): Resource<List<Order>>
  suspend fun getOrder(orderId: String): Resource<Order>
  suspend fun getOrderFilters(): Resource<List<Filter>>
  suspend fun createOrder(request: CreateOrderRequest): Resource<CreateOrderResponse>
  suspend fun cancelOrder(orderId: String): Resource<Nothing>
  suspend fun confirmOrderArrived(orderId: String): Resource<Nothing>
}