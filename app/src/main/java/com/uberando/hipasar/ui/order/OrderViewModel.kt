package com.uberando.hipasar.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.CartRepository
import com.uberando.hipasar.data.repository.OrderRepository
import com.uberando.hipasar.data.repository.PaymentRepository
import com.uberando.hipasar.entity.request.PaymentRequest
import com.uberando.hipasar.entity.request.order.CreateOrderRequest
import com.uberando.hipasar.ui.BaseViewModel
import com.uberando.hipasar.util.Event
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityScoped
class OrderViewModel @Inject constructor(
  private val orderRepository: OrderRepository,
  private val paymentRepository: PaymentRepository,
  private val cartRepository: CartRepository
) : BaseViewModel() {

  private var _createOrderRequest: CreateOrderRequest? = null

  private var _paymentRequest: PaymentRequest? = null

  private var _orderId: String? = null

  private var _orderCode: String? = null

  private var _paymentUrl: String? = null

  private val _createOrderSuccess = MutableLiveData<Event<Boolean>>()
  val createOrderSuccess: LiveData<Event<Boolean>> get() = _createOrderSuccess

  private val _requestPaymentSuccess = MutableLiveData<Event<Boolean>>()
  val requestPaymentSuccess: LiveData<Event<Boolean>> get() = _requestPaymentSuccess

  private val _requireToPayOnOtherPlatform = MutableLiveData<Event<Boolean>>()
  val requireToPayOnOtherPlatform: LiveData<Event<Boolean>> get() = _requireToPayOnOtherPlatform

  fun saveCreateOrderRequest(request: CreateOrderRequest) {
    _createOrderRequest = request
  }

  fun savePaymentRequest(request: PaymentRequest) {
    _paymentRequest = request
  }

  fun beginOrderFlow(withPayment: Boolean) {
    viewModelScope.launch {
      createOrder(withPayment)
    }
  }

  private suspend fun createOrder(withPayment: Boolean) = withContext(Dispatchers.IO) {
    _createOrderRequest?.let { createOrderRequest ->
      orderRepository.createOrder(createOrderRequest).let { resource ->
        when (resource) {
          is Resource.Success -> {
            _orderId = resource.data?.id
            _orderCode = resource.data?.orderCode
            if (withPayment) {
              requestPayment()
            } else {
              _requestPaymentSuccess.postValue(Event(true))
            }
          }
          is Resource.Failed -> {
            _createOrderSuccess.postValue(Event(false))
          }
        }
      }
    }
  }

  private suspend fun requestPayment() = withContext(Dispatchers.IO) {
    _paymentRequest?.let { paymentRequest ->
      paymentRepository.requestPayment(paymentRequest.also { it.orderId = _orderId ?: "" }).let { resource ->
        when (resource) {
          is Resource.Success -> {
            clearCart()
            _requestPaymentSuccess.postValue(Event(true))
            if (resource.data?.response?.paymentUrl != null) {
              _paymentUrl = resource.data.response.paymentUrl
              _requireToPayOnOtherPlatform.postValue(Event(true))
            }
          }
          is Resource.Failed -> {
            _requestPaymentSuccess.postValue(Event(false))
          }
        }
      }
    }
  }

  private suspend fun clearCart() =
    withContext(Dispatchers.IO) {
      cartRepository.clearCart()
    }

  fun requirePaymentUrl() = _paymentUrl ?: ""

}