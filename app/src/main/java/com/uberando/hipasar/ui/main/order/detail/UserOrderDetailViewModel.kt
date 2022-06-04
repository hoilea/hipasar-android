package com.uberando.hipasar.ui.main.order.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.OrderRepository
import com.uberando.hipasar.entity.Order
import com.uberando.hipasar.ui.BaseViewModel
import com.uberando.hipasar.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class UserOrderDetailViewModel @Inject constructor(
  private val orderRepository: OrderRepository
) : BaseViewModel() {
  
  private val _order = MutableLiveData<Order>()
  val order: LiveData<Order> get() = _order

  val orderDeliveryTime = Transformations.map(_order) {
    it.deliveryTime.buildSingleLineText()
  }
  
  private val _showActionButton = MutableLiveData<Boolean>()
  val showActionButton: LiveData<Boolean> get() = _showActionButton
  
  private val _showCancelButton = MutableLiveData<Boolean>()
  val showCancelButton: LiveData<Boolean> get() = _showCancelButton
  
  private val _requireToPayOrder = MutableLiveData<Event<Boolean>>()
  val requireToPayOrder: LiveData<Event<Boolean>> get() = _requireToPayOrder
  
  private val _whenUpdateOrderFailed = MutableLiveData<Event<Boolean>>()
  val whenUpdateOrderFailed: LiveData<Event<Boolean>> get() = _whenUpdateOrderFailed
  
  private val _whenBackButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenBackButtonClicked: LiveData<Event<Boolean>> get() = _whenBackButtonClicked
  
  private val _whenLoginButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenLoginButtonClicked: LiveData<Event<Boolean>> get() = _whenLoginButtonClicked
  
  private val _whenCancelOrderSuccess = MutableLiveData<Event<Boolean>>()
  val whenCancelOrderSuccess: LiveData<Event<Boolean>> get() = _whenCancelOrderSuccess
  
  val showContentContainer = Transformations.map(_order) { it != null }
  
  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator
  
  private val _showLoadingDialog = MutableLiveData<Boolean>()
  val showLoadingDialog: LiveData<Boolean> get() = _showLoadingDialog
  
  private val _showErrorContainer = MutableLiveData<Boolean>()
  val showErrorContainer: LiveData<Boolean> get() = _showErrorContainer
  
  private val _showNotFoundContainer = MutableLiveData<Boolean>()
  val showNotFoundContainer: LiveData<Boolean> get() = _showNotFoundContainer
  
  private val _showUnauthorizedContainer = MutableLiveData<Boolean>()
  val showUnauthorizedContainer: LiveData<Boolean> get() = _showUnauthorizedContainer
  
  private var cachedOrderId = ""
  
  private fun getSingleOrder(orderId: String) {
    viewModelScope.launch {
      doActionWithLoadingIndicator(
        givenContext = Dispatchers.IO,
        propertyTarget = _showLoadingIndicator,
        onBeforeAction = { hideContainers() }
      ) {
        orderRepository.getOrder(orderId).let { resource ->
          when (resource) {
            is Resource.Success -> {
              _order.postValue(resource.data)
              updateStatus(resource.data!!.status)
              Timber.d("received order: ${resource.data}")
            }
            is Resource.Failed -> {
              when (resource.code) {
                Constants.CODE_NOT_FOUND -> _showNotFoundContainer.postValue(true)
                Constants.CODE_UNAUTHORIZED -> _showUnauthorizedContainer.postValue(true)
                else -> _showErrorContainer.postValue(true)
              }
            }
          }
        }
      }
    }
  }
  
  fun requireToInitializeOrder(orderId: String) {
    cachedOrderId = orderId
    getSingleOrder(cachedOrderId)
    Timber.d("cached order id: $orderId")
  }
  
  fun requireToGetOrder() {
    getSingleOrder(cachedOrderId)
  }
  
  fun requireToCancelOrder(orderId: String) {
    Timber.i("onRequireToCancelOrder: withId: $orderId")
    viewModelScope.launch {
      doActionWithLoadingIndicator(Dispatchers.IO, _showLoadingDialog) {
        orderRepository.cancelOrder(orderId).let { resource ->
          when (resource) {
            is Resource.Success -> {
              getSingleOrder(cachedOrderId)
              _whenCancelOrderSuccess.postValue(Event(true))
            }
            is Resource.Failed -> _whenCancelOrderSuccess.postValue(Event(false))
          }
        }
      }
    }
  }
  
  fun requireToUpdateOrder(orderId: String, orderStatus: Int) {
    if (orderStatus == Constants.ORDER_STATUS_PENDING) {
      _requireToPayOrder.postValue(Event(true))
    } else {
      if (orderStatus == Constants.ORDER_STATUS_ON_DELIVERY) {
        requireToFinishOrder(orderId)
      }
    }
  }
  
  fun requireToQuietlyUpdateOrderStatus(cancel: Boolean = false) {
    _order.value!!.let {
      var tempStatus = it.status
      if (cancel) {
        tempStatus = Constants.ORDER_STATUS_CANCELLED
      } else {
        tempStatus += 1
      }
      it.status = tempStatus
      updateStatus(it.status)
      _order.postValue(it)
    }
  }
  
  private fun requireToFinishOrder(orderId: String) {
    viewModelScope.launch(Dispatchers.IO) {
      doActionWithLoadingIndicator(Dispatchers.IO, _showLoadingDialog) {
        orderRepository.confirmOrderArrived(orderId).let { resource ->
          when (resource) {
            is Resource.Success -> {
              _order.postValue(resource.data)
              updateStatus(Constants.ORDER_STATUS_FINISHED)
            }
            is Resource.Failed -> {
              _whenUpdateOrderFailed.postValue(Event(true))
            }
          }
        }
      }
    }
  }
  
  fun requirePaymentArguments(): String {
    return mapOf(
      Constants.KEY_ORDER_ID to _order.value!!.id,
      Constants.KEY_ORDER_CODE to _order.value!!.orderCode,
      Constants.KEY_ORDER_PRICE to _order.value!!.total
    ).toString()
  }
  
  private fun hideContainers() {
    _showErrorContainer.postValue(false)
    _showUnauthorizedContainer.postValue(false)
    _showNotFoundContainer.postValue(false)
  }
  
  private fun updateStatus(status: Int) {
    updateShowActionButton(status.determineActionButtonVisibility())
    updateShowCancelButton(status.determineCancelButtonVisibility())
  }
  
  private fun updateShowActionButton(state: Boolean) {
    _showActionButton.postValue(state)
  }
  
  private fun updateShowCancelButton(state: Boolean) {
    _showCancelButton.postValue(state)
  }
  
  fun onRetryButtonClick() {
    getSingleOrder(cachedOrderId)
  }
  
  fun onBackButtonClick() {
    _whenBackButtonClicked.postValue(Event(true))
  }
  
  fun onLoginButtonClick() {
    _whenLoginButtonClicked.postValue(Event(true))
  }
  
}