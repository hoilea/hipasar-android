package com.uberando.hipasar.data.source

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.OrderRepository
import com.uberando.hipasar.data.source.local.dao.AccountDao
import com.uberando.hipasar.data.source.remote.OrderService
import com.uberando.hipasar.entity.Filter
import com.uberando.hipasar.entity.Order
import com.uberando.hipasar.entity.request.order.CreateOrderRequest
import com.uberando.hipasar.entity.response.CreateOrderResponse
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.makeBearerToken
import javax.inject.Inject

class OrderDataSource @Inject constructor(
  private val orderService: OrderService,
  private val accountDao: AccountDao
) : OrderRepository {
  
  private val cachedOrders = mutableListOf<Order>()

  override suspend fun getOrders(): Resource<List<Order>> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          orderService.getOrders(token).let { response ->
            cachedOrders.clearAndAdd(response).run { Resource.Success(response) }
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }
  
  override suspend fun getOrdersByFilters(filterCodes: List<Int>): Resource<List<Order>> {
    return try {
      if (filterCodes.isNotEmpty()) {
        Resource.Success(
          cachedOrders.filter { order ->
            order.status == filterCodes.find { order.status == it }
          })
      } else {
        Resource.Success(cachedOrders)
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }
  
  override suspend fun getOrder(orderId: String): Resource<Order> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          orderService.getOrder(token, orderId).let { response ->
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
  
  override suspend fun getOrderFilters(): Resource<List<Filter>> {
    return Resource.Success(availableFilters())
  }
  
  override suspend fun createOrder(request: CreateOrderRequest): Resource<CreateOrderResponse> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          orderService.createOrder(token, request).let { response ->
            if (response.orderCode.isNotBlank() && response.orderCode.isNotEmpty()) {
              Resource.Success(response)
            } else {
              Resource.Failed(code = Constants.CODE_INTERNAL_SERVER_ERROR)
            }
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun cancelOrder(orderId: String): Resource<Nothing> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          orderService.cancelOrder(token, orderId).let { responseState ->
            if (responseState.success) {
              Resource.Success()
            } else {
              Resource.Failed(code = Constants.CODE_INTERNAL_SERVER_ERROR)
            }
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun confirmOrderArrived(orderId: String): Resource<Nothing> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          orderService.confirmOrderArrived(token, orderId).let { responseState ->
            if (responseState.success) {
              Resource.Success()
            } else {
              Resource.Failed(code = Constants.CODE_INTERNAL_SERVER_ERROR)
            }
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  private suspend fun getTokenOrNull(): String? = accountDao.getSingleAccount()?.token?.makeBearerToken()
  
  private fun MutableList<Order>.clearAndAdd(orders: List<Order>) =
    this.clear().also { this.addAll(orders) }
  
  private fun availableFilters() =
    listOf(
      Filter(
        code = Constants.ORDER_STATUS_PENDING,
        name = "Pending"
      ),
      Filter(
        code = Constants.ORDER_STATUS_ADMIN_CONFIRMATION,
        name = "Admin confirmation"
      ),
      Filter(
        code = Constants.ORDER_STATUS_ON_PROCESS,
        name = "On process"
      ),
      Filter(
        code = Constants.ORDER_STATUS_ON_DELIVERY,
        name = "On delivery"
      ),
      Filter(
        code = Constants.ORDER_STATUS_ARRIVED,
        name = "Arrived"
      ),
      Filter(
        code = Constants.ORDER_STATUS_FINISHED,
        name = "Finished"
      ),
      Filter(
        code = Constants.ORDER_STATUS_CANCELLATION_REQUEST,
        name = "Cancellation request"
      ),
      Filter(
        code = Constants.ORDER_STATUS_CANCELLED,
        name = "Cancelled"
      )
    )

}
