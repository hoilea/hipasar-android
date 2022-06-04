package com.uberando.hipasar.ui.main.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.OrderRepository
import com.uberando.hipasar.entity.Filter
import com.uberando.hipasar.entity.Order
import com.uberando.hipasar.entity.OrderType
import com.uberando.hipasar.ui.BaseViewModel
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.Event
import com.uberando.hipasar.util.asOrderType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class UserOrderViewModel @Inject constructor(
  private val orderRepository: OrderRepository
) : BaseViewModel() {
  
  private val _orders = MutableLiveData<List<OrderType>>()
  val orders: LiveData<List<OrderType>> get() = _orders
  
  private val _filters = MutableLiveData<List<Filter>>()
  val filters: LiveData<List<Filter>> get() = _filters

  private val _filter = MutableLiveData<Filter>()
  val filter: LiveData<Filter> get() = _filter
  
  private val _getFilterFailed = MutableLiveData<Event<Boolean>>()
  val getFilterFailed: LiveData<Event<Boolean>> get() = _getFilterFailed
  
  private val _cancelOrderFailed = MutableLiveData<Event<Boolean>>()
  val cancelOrderFailed: LiveData<Event<Boolean>> get() = _cancelOrderFailed
  
  private val _confirmOrderFailed = MutableLiveData<Event<Boolean>>()
  val confirmOrderFailed: LiveData<Event<Boolean>> get() = _confirmOrderFailed

  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator

  private val _showUnauthorizedContainer = MutableLiveData<Boolean>()
  val showUnauthorizedContainer: LiveData<Boolean> get() = _showUnauthorizedContainer

  private val _showContentContainer = MutableLiveData<Boolean>()
  val showContentContainer: LiveData<Boolean> get() = _showContentContainer

  private val _showEmptyContainer = MutableLiveData<Boolean>()
  val showEmptyContainer: LiveData<Boolean> get() = _showEmptyContainer

  private val _showErrorContainer = MutableLiveData<Boolean>()
  val showErrorContainer: LiveData<Boolean> get() = _showErrorContainer
  
  private val _showFilterResultEmpty = MutableLiveData<Boolean>()
  val showFilterResultEmpty: LiveData<Boolean> get() = _showFilterResultEmpty

  private val _whenLoginButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenLoginButtonClicked: LiveData<Event<Boolean>> get() = _whenLoginButtonClicked

  private val _whenBackButtonClicked = MutableLiveData<Boolean>()
  val whenBackButtonClicked: LiveData<Boolean> get() = _whenBackButtonClicked

  fun setupOrderFilter(filter: Filter) {
    _filter.postValue(filter)
    getOrders()
  }

  private fun hideAllContainers() {
    _showUnauthorizedContainer.postValue(false)
    _showContentContainer.postValue(false)
    _showEmptyContainer.postValue(false)
    _showErrorContainer.postValue(false)
  }

  private fun getOrders() {
    viewModelScope.launch {
      doActionWithLoadingIndicator(
        givenContext = Dispatchers.IO,
        propertyTarget = _showLoadingIndicator,
        onBeforeAction = { hideAllContainers() }
      ) {
        orderRepository.getOrders().let { resource ->
          when (resource) {
            is Resource.Success -> processOrderData(resource.data)
            is Resource.Failed -> {
              when (resource.code) {
                Constants.CODE_UNAUTHORIZED -> _showUnauthorizedContainer.postValue(true)
                else -> _showErrorContainer.postValue(true)
              }
            }
          }
        }
      }
    }
  }
  
  private fun getFilters() {
    viewModelScope.launch {
      orderRepository.getOrderFilters().let { resource ->
        when (resource) {
          is Resource.Success -> _filters.postValue(resource.data)
          is Resource.Failed -> _getFilterFailed.postValue(Event(true))
        }
      }
    }
  }

  fun onLoginButtonClick() {
    _whenLoginButtonClicked.postValue(Event(true))
  }

  fun onRetryButtonClick() {
    getOrders()
  }

  fun requireToRefreshOrder() {
    Timber.i("require to refresh order")
    getOrders()
  }
  
  fun requireToFilterOrders(ids: List<Int>) {
    viewModelScope.launch {
      doActionWithLoadingIndicator(
        givenContext = Dispatchers.IO,
        propertyTarget = _showLoadingIndicator
      ) {
        orderRepository.getOrdersByFilters(ids).let { resource ->
          when (resource) {
            is Resource.Success -> processOrderData(resource.data, PROCESS_TYPE_FILTER)
            is Resource.Failed -> {
              when (resource.code) {
                Constants.CODE_UNAUTHORIZED -> _showUnauthorizedContainer.postValue(true)
                else -> _showErrorContainer.postValue(true)
              }
            }
          }
        }
      }
    }
  }
  
  fun requireToCancelOrder(order: Order) {
    viewModelScope.launch(Dispatchers.IO) {
      orderRepository.cancelOrder(order.id).let { resource ->
        when (resource) {
          is Resource.Success -> getOrders()
          is Resource.Failed -> _cancelOrderFailed.postValue(Event(true))
        }
      }
    }
  }
  
  fun requireToConfirmArrivedOrder(order: Order) {
    viewModelScope.launch(Dispatchers.IO) {
      orderRepository.confirmOrderArrived(order.id).let { resource ->
        when (resource) {
          is Resource.Success -> getOrders()
          is Resource.Failed -> _confirmOrderFailed.postValue(Event(true))
        }
      }
    }
  }
  
  private fun processOrderData(orders: List<Order>?, processType: Int = PROCESS_TYPE_REQUEST) {
    if (orders != null && orders.isNotEmpty()) {
      _orders.postValue(emptyList())
      if (_filter.value?.code ?: 0 != -1) {
        _orders.postValue(orders.filter { it.status == _filter.value?.code }.asOrderType())
      } else {
        _orders.postValue(orders.asOrderType())
      }
      if (processType == PROCESS_TYPE_FILTER) {
        _showFilterResultEmpty.postValue(false)
      } else if (processType == PROCESS_TYPE_REQUEST) {
        _showContentContainer.postValue(true)
      }
    } else {
      _orders.postValue(emptyList())
      if (processType == PROCESS_TYPE_FILTER) {
        _showFilterResultEmpty.postValue(true)
      } else if (processType == PROCESS_TYPE_REQUEST) {
        _showEmptyContainer.postValue(true)
      }
    }
  }

  fun onBackButtonClick() {
    _whenBackButtonClicked.postValue(true)
  }
  
  companion object {
    private const val PROCESS_TYPE_REQUEST = 1
    private const val PROCESS_TYPE_FILTER = 2
  }

}