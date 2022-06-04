package com.uberando.hipasar.ui.main.order.option

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.OrderRepository
import com.uberando.hipasar.entity.Filter
import com.uberando.hipasar.entity.Order
import com.uberando.hipasar.exception.OrderException
import com.uberando.hipasar.exception.OrderFilterException
import com.uberando.hipasar.ui.BaseViewModel
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.Event
import com.uberando.hipasar.util.getSizeOrZero
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class UserOrderOptionViewModel @Inject constructor(
  private val orderRepository: OrderRepository
) : BaseViewModel() {
  
  private val _orderFilters = MutableLiveData<List<Filter>>()
  val orderFilters: LiveData<List<Filter>> get() = _orderFilters

  private val _orders = mutableListOf<Order>()
  
  private val _orderFiltersReady = MutableLiveData<Event<Boolean>>()
  val orderFiltersReady: LiveData<Event<Boolean>> get() = _orderFiltersReady
  
  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator
  
  private val _showContentContainer = MutableLiveData<Boolean>()
  val showContentContainer: LiveData<Boolean> get() = _showContentContainer
  
  private val _showErrorContainer = MutableLiveData<Boolean>()
  val showErrorContainer: LiveData<Boolean> get() = _showErrorContainer

  private val _showUnauthorizedContainer = MutableLiveData<Boolean>()
  val showUnauthorizedContainer: LiveData<Boolean> get() = _showUnauthorizedContainer
  
  init {
    getData()
  }
  
  private fun getData() {
    viewModelScope.launch {
      doActionWithLoadingIndicator(
        givenContext = Dispatchers.IO,
        propertyTarget = _showLoadingIndicator,
        onBeforeAction = { hideContainers() }
      ) {
        val successKeys = mutableListOf<Int>()
        val filterJob = launch { successKeys.add(getOrderFilters()) }
        val orderJob = launch { successKeys.add(getOrders()) }
        val computationJob = launch(start = CoroutineStart.LAZY) { computeAvailableOrders() }
        try {
          joinAll(filterJob, orderJob)
          computationJob.join()
        } catch (e: OrderException) {
          Timber.e(e)
        } catch (e: OrderFilterException) {
          Timber.e(e)
        } catch (e: ArithmeticException) {
          Timber.e(e)
        }
      }
    }
  }
  
  private suspend fun getOrders(): Int {
    Timber.i("getOrders")
    return withContext(Dispatchers.IO) {
      orderRepository.getOrders().let { resource ->
        when (resource) {
          is Resource.Success -> {
            _orders.addAll(resource.data ?: emptyList())
            GET_ORDER_SUCCESS_KEY
          }
          is Resource.Failed -> {
            when (resource.code) {
              Constants.CODE_UNAUTHORIZED -> {
                _showUnauthorizedContainer.postValue(true)
                -1
              }
              else -> {
                -1
              }
            }
          }
        }
      }
    }
  }
  
  private suspend fun getOrderFilters(): Int {
    Timber.i("getOrderFilters")
    return withContext(Dispatchers.IO) {
      orderRepository.getOrderFilters().let { resource ->
        when (resource) {
          is Resource.Success -> {
            _orderFilters.postValue(resource.data)
            _showContentContainer.postValue(true)
            Timber.i("getOrderFiltes: success")
            GET_FILTER_SUCCESS_KEY
          }
          is Resource.Failed -> throw OrderFilterException()
        }
      }
    }
  }

  private suspend fun computeAvailableOrders() {
    withContext(Dispatchers.Default) {
      _orders.groupBy { order -> order.status }.let { group ->
        _orderFilters.postValue(
          _orderFilters.value?.map { filter ->
            filter.also { it.orderCount = group[it.code]?.size ?: 0 }
          }?.toMutableList()?.withOrderAllType()
        )
        _orderFiltersReady.postValue(Event(true))
      }
    }
  }
  
  private fun hideContainers() {
    _showContentContainer.postValue(false)
  }

  private fun MutableList<Filter>.withOrderAllType() =
    this.also { it.add(0, Filter(-1, "All", _orders.getSizeOrZero())) }
  
  fun getFilters() = _orderFilters.value
  
  companion object {
    private const val GET_FILTER_SUCCESS_KEY = 1
    private const val GET_ORDER_SUCCESS_KEY = 2
  }
  
}