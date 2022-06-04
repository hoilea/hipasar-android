package com.uberando.hipasar.ui.order.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.CartRepository
import com.uberando.hipasar.data.repository.CheckoutRepository
import com.uberando.hipasar.entity.*
import com.uberando.hipasar.entity.request.PaymentRequest
import com.uberando.hipasar.entity.request.order.CreateOrderRequest
import com.uberando.hipasar.entity.request.order.OrderProductRequest
import com.uberando.hipasar.ui.BaseViewModel
import com.uberando.hipasar.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class OrderCheckoutViewModel @Inject constructor(
  private val cartRepository: CartRepository,
  private val checkoutRepository: CheckoutRepository
) : BaseViewModel() {
  
  /**
   * Data holder
   */
  private val _addressSectionButtonText = MutableLiveData<String>()
  val addressSelectionButtonText: LiveData<String> get() = _addressSectionButtonText
  
  private val _addresses = MutableLiveData<List<Address>>()
  val addresses: LiveData<List<Address>> get() = _addresses
  
  private val _selectedAddress = MutableLiveData<Address>()
  val selectedAddress: LiveData<Address> get() = _selectedAddress
  
  private val _orderProducts = MutableLiveData<List<OrderProduct>>()
  val orderProducts: LiveData<List<OrderProduct>> get() = _orderProducts
  
  private val _subtotal = MutableLiveData<Int>()
  val subtotal: LiveData<Int> get() = _subtotal
  
  private val _tax = MutableLiveData<Int>()
  val tax: LiveData<Int> get() = _tax
  
  private val _shippingFee = MutableLiveData<Int>()
  val shippingFee: LiveData<Int> get() = _shippingFee
  
  private val _totalPrice = MutableLiveData<Int>()
  val totalPrice: LiveData<Int> get() = _totalPrice
  
  private val _taxPercentage = MutableLiveData<Int>()
  val taxPercentage: LiveData<Int> get() = _taxPercentage
  
  private val _shippingIsFree = MutableLiveData<Boolean>()
  val shippingIsFree: LiveData<Boolean> get() = _shippingIsFree

  private val _paymentMethod = MutableLiveData<PaymentMethod>()
  val paymentMethod: LiveData<PaymentMethod> get() = _paymentMethod

  private val _deliveryTime = MutableLiveData<List<DeliveryTime>>()
  val deliveryTime: LiveData<List<DeliveryTime>> get() = _deliveryTime

  private val _deliveryDate = MutableLiveData<List<String>>()
  val deliveryDate: LiveData<List<String>> get() = _deliveryDate

  private val _selectedDeliveryTime = MutableLiveData<DeliveryTime>()

  val selectedDeliveryTimeText = Transformations.map(_selectedDeliveryTime) {
    it.buildSingleLineText()
  }

  private val _selectedDeliveryDate = MutableLiveData<String>()
  val selectedDeliveryDate: LiveData<String> get() = _selectedDeliveryDate
  
  /**
   * variable to hold what data fetch that error. So when user click retry button,
   * it will just retry fetch error only data.
   */
  private val _failedRegistry = mutableListOf<Int>()
  
  private var completedTask = 0

  private var _afterCreate = false
  
  /**
   * Visibility states
   */
  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator
  
  private val _showErrorContainer = MutableLiveData<Boolean>()
  val showErrorContainer: LiveData<Boolean> get() = _showErrorContainer
  
  private val _showContentContainer = MutableLiveData<Boolean>()
  val showContentContainer: LiveData<Boolean> get() = _showContentContainer
  
  val showAddressContainer = Transformations.map(_selectedAddress) {
    it != null
  }
  
  private val _showPriceComputeLoadingIndicator = MutableLiveData<Boolean>()
  val showPriceComputeLoadingIndicator: LiveData<Boolean> get() = _showPriceComputeLoadingIndicator
  
  private val _showCreateOrderLoadingIndicator = MutableLiveData<Boolean>()
  val showCreateOrderLoadingIndicator: LiveData<Boolean> get() = _showCreateOrderLoadingIndicator

  val paymentMethodSelected: LiveData<Boolean> = Transformations.map(_paymentMethod) {
    it != null
  }

  val deliveryTimeSelected = Transformations.map(_selectedDeliveryTime) {
    it != null
  }

  val deliveryDateSelected = Transformations.map(selectedDeliveryDate) {
    it != null
  }
  
  /**
   * Action states
   */
  private val _whenBackButtonClicked = MutableLiveData<Boolean>()
  val whenBackButtonClicked: LiveData<Boolean> get() = _whenBackButtonClicked
  
  private val _whenChangeSelectAddressClicked = MutableLiveData<Event<Boolean>>()
  val whenChangeSelectAddressClicked: LiveData<Event<Boolean>> get() = _whenChangeSelectAddressClicked
  
  private val _whenAddressNotSelected = MutableLiveData<Event<Boolean>>()
  val whenAddressNotSelected: LiveData<Event<Boolean>> get() = _whenAddressNotSelected

  private val _whenChangePaymentMethodClicked = MutableLiveData<Event<Boolean>>()
  val whenChangePaymentMethodClicked: LiveData<Event<Boolean>> get() = _whenChangePaymentMethodClicked

  private val _whenPaymentMethodNotSelected = MutableLiveData<Event<Boolean>>()
  val whenPaymentMethodNotSelected: LiveData<Event<Boolean>> get() = _whenPaymentMethodNotSelected

  private val _whenRequireToProcessOrder = MutableLiveData<Event<Boolean>>()
  val whenRequireToProcessOrder: LiveData<Event<Boolean>> get() = _whenRequireToProcessOrder

  private val _whenSelectDeliveryTimeClicked = MutableLiveData<Event<Boolean>>()
  val whenSelectDeliveryTimeClicked: LiveData<Event<Boolean>> get() = _whenSelectDeliveryTimeClicked

  private val _whenDeliveryTimeNotSelected = MutableLiveData<Event<Boolean>>()
  val whenDeliveryTimeNotSelected: LiveData<Event<Boolean>> get() = _whenDeliveryTimeNotSelected

  private val _whenSelectDeliveryDateClicked = MutableLiveData<Event<Boolean>>()
  val whenSelectDeliveryDateClicked: LiveData<Event<Boolean>> get() = _whenSelectDeliveryDateClicked

  private val _whenDeliveryDateNotSelected = MutableLiveData<Event<Boolean>>()
  val whenDeliveryDateNotSelected: LiveData<Event<Boolean>> get() = _whenDeliveryDateNotSelected

  init {
    beginFetchDataProcess()
  }
  
  private fun hideContainers() {
    _showContentContainer.postValue(false)
    _showErrorContainer.postValue(false)
  }
  
  private fun beginFetchDataProcess() {
    viewModelScope.launch {
      doActionWithLoadingIndicator(
        givenContext = Dispatchers.Unconfined,
        propertyTarget = _showLoadingIndicator,
        onBeforeAction = { hideContainers() },
        onAfterAction = { determineViewVisibility() }
      ) {
        getAddresses().also { completedTask += it }
        getProducts().also { completedTask += it }
        getFees().also { completedTask += it }
        getDeliveryTime().also { completedTask += it }
        getDeliveryDate().also { completedTask += it }
      }
      computeTotalWeight()
      computeInvoice()
    }
  }
  
  private suspend fun getAddresses(): Int {
    return withContext(Dispatchers.IO) {
      checkoutRepository.getAddresses().let { resource ->
        when (resource) {
          is Resource.Success -> {
            _addresses.postValue(resource.data ?: emptyList())
            _selectedAddress.postValue(resource.data?.selectPrimary() ?: determineSelectedAddress(resource.data ?: emptyList()))
            Constants.SUCCESS
          }
          is Resource.Failed -> {
            _failedRegistry.add(FAILED_FETCH_ADDRESS)
            Constants.FAILED
          }
        }
      }
    }
  }
  
  private suspend fun getProducts(): Int {
    return withContext(Dispatchers.IO) {
      cartRepository.getCart().let { resource ->
        when (resource) {
          is Resource.Success -> {
            _orderProducts.postValue(resource.data?.products?.toOrderProducts())
            Constants.SUCCESS
          }
          is Resource.Failed -> {
            _failedRegistry.add(FAILED_FETCH_PRODUCTS)
            Constants.FAILED
          }
        }
      }
    }
  }
  
  private suspend fun getFees(): Int {
    return withContext(Dispatchers.IO) {
      checkoutRepository.getFees().let { resource ->
        when (resource) {
          is Resource.Success -> {
            _shippingFee.postValue(resource.data?.shipmentFee)
            _taxPercentage.postValue(resource.data?.taxPercentage)
            Constants.SUCCESS
          }
          is Resource.Failed -> {
            _failedRegistry.add(FAILED_FETCH_FEES)
            Constants.FAILED
          }
        }
      }
    }
  }

  private suspend fun getDeliveryTime(): Int {
    return withContext(Dispatchers.IO) {
      checkoutRepository.getDeliveryTime().let { resource ->
        when (resource) {
          is Resource.Success -> {
            _deliveryTime.postValue(resource.data ?: emptyList())
            Constants.SUCCESS
          }
          is Resource.Failed -> {
            _failedRegistry.add(FAILED_FETCH_DELIVERY_TIME)
            Constants.FAILED
          }
        }
      }
    }
  }

  private suspend fun getDeliveryDate(): Int {
    return withContext(Dispatchers.IO) {
      checkoutRepository.getDeliveryDate().let { resource ->
        when (resource) {
          is Resource.Success -> {
            _deliveryDate.postValue(resource.data ?: emptyList())
            Constants.SUCCESS
          }
          is Resource.Failed -> {
            _failedRegistry.add(FAILED_FETCH_DELIVERY_DATE)
            Constants.FAILED
          }
        }
      }
    }
  }
  
  private suspend fun computeInvoice() {
    Timber.i("compute invoice")
    doActionWithLoadingIndicator(Dispatchers.IO, _showPriceComputeLoadingIndicator) {
      _orderProducts.value?.let { orderProducts ->
        val tempSubtotal = orderProducts.computeTotalPrice()
//        val tempTax = (tempSubtotal * _taxPercentage.valueOrZero()) / 100
        val tempTax = 0
        val tempFinalPrice = tempSubtotal + tempTax + _shippingFee.valueOrZero()
        _subtotal.postValue(tempSubtotal)
        _tax.postValue(tempTax)
        _totalPrice.postValue(tempFinalPrice)
      }
    }
  }
  
  private suspend fun computeTotalWeight() {
    withContext(Dispatchers.Default) {
      var tempWeight = 0
      _orderProducts.value?.forEach { product ->
        tempWeight += (product.unitPerGram * product.qty)
      }
      if (tempWeight >= MINIMUM_FREE_WEIGHT) {
        _shippingIsFree.postValue(true)
        _shippingFee.postValue(0)
      } else {
        _shippingIsFree.postValue(false)
      }
    }
  }
  
  private fun determineViewVisibility() {
    if (completedTask == 5) {
      _showContentContainer.postValue(true)
    } else {
      _showErrorContainer.postValue(true)
    }
  }

  private suspend fun determineSelectedAddress(addresses: List<Address>): Address? {
    return withContext(Dispatchers.Default) {
      if (addresses.isNotEmpty()) {
        if (_afterCreate) {
          addresses.last()
        } else {
          addresses.first()
        }
      } else {
        null
      }
    }
  }
  
  private fun beginRetryProcess() {
    viewModelScope.launch {
      when {
        // maximum size is always 3, because it's only have 3 request function
        _failedRegistry.size == 3 -> {
          beginFetchDataProcess()
        }
        _failedRegistry.size == 2 -> {
          when {
            _failedRegistry.containsAll(listOf(FAILED_FETCH_ADDRESS, FAILED_FETCH_FEES)) -> {
              doActionWithLoadingIndicator(propertyTarget = _showLoadingIndicator, onAfterAction = { determineViewVisibility() }) {
                getAddresses().also { completedTask += it }
                getFees().also { completedTask += it }
              }
            }
            _failedRegistry.containsAll(listOf(FAILED_FETCH_ADDRESS, FAILED_FETCH_PRODUCTS)) -> {
              doActionWithLoadingIndicator(propertyTarget = _showLoadingIndicator, onAfterAction = { determineViewVisibility() }) {
                getAddresses().also { completedTask += it }
                getProducts().also { completedTask += it }
              }
            }
            _failedRegistry.containsAll(listOf(FAILED_FETCH_PRODUCTS, FAILED_FETCH_FEES)) -> {
              doActionWithLoadingIndicator(propertyTarget = _showLoadingIndicator, onAfterAction = { determineViewVisibility() }) {
                getFees().also { completedTask += it }
                getProducts().also { completedTask += it }
              }
            }
          }
        }
        _failedRegistry.first() == FAILED_FETCH_ADDRESS -> {
          doActionWithLoadingIndicator(propertyTarget = _showLoadingIndicator) { getAddresses() }
        }
        _failedRegistry.first() == FAILED_FETCH_PRODUCTS -> {
          doActionWithLoadingIndicator(propertyTarget = _showLoadingIndicator) { getProducts() }
        }
        _failedRegistry.first() == FAILED_FETCH_FEES -> {
          doActionWithLoadingIndicator(propertyTarget = _showLoadingIndicator) { getFees() }
        }
        _failedRegistry.first() == FAILED_FETCH_DELIVERY_TIME -> {
          doActionWithLoadingIndicator(propertyTarget = _showLoadingIndicator) { getDeliveryTime() }
        }
        _failedRegistry.first() == FAILED_FETCH_DELIVERY_DATE -> {
          doActionWithLoadingIndicator(propertyTarget = _showLoadingIndicator) { getDeliveryDate() }
        }
      }
    }
  }
  
  private fun beginOrderProcess() {
    viewModelScope.launch {
      when {
        _selectedAddress.value == null -> {
          _whenAddressNotSelected.postValue(Event(true))
        }
        _paymentMethod.value == null -> {
          _whenPaymentMethodNotSelected.postValue(Event(true))
        }
        _selectedDeliveryTime.value == null -> {
          _whenDeliveryTimeNotSelected.postValue(Event(true))
        }
        _selectedDeliveryDate.value == null -> {
          _whenDeliveryDateNotSelected.postValue(Event(true))
        }
        else -> {
          _whenRequireToProcessOrder.postValue(Event(true))
        }
      }
    }
  }
  
  fun setupAddressSectionButtonText(text: String) {
    _addressSectionButtonText.postValue(text)
  }
  
  fun setupSelectedAddress(selectedId: Int) {
    _addresses.value?.let { addresses ->
      addresses.find { address ->
        address.id == selectedId
      }.let {
        _selectedAddress.postValue(it)
      }
    }
  }

  fun setupAfterCreate(afterCrate: Boolean) {
    _afterCreate = afterCrate
  }

  fun setupPaymentMethod(paymentMethod: PaymentMethod) {
    _paymentMethod.postValue(paymentMethod)
  }

  fun setupDeliveryTime(deliveryTime: DeliveryTime) {
    _selectedDeliveryTime.postValue(deliveryTime)
  }

  fun setupDeliveryDate(deliveryDate: String) {
    _selectedDeliveryDate.postValue(deliveryDate)
  }
  
  fun onRetryButtonClick() {
    beginRetryProcess()
  }

  fun onPayButtonClick() {
    beginOrderProcess()
  }

  fun onBackButtonClick() {
    _whenBackButtonClicked.postValue(true)
  }

  fun onChangeSelectAddressClick() {
    _whenChangeSelectAddressClicked.postValue(Event(true))
  }

  fun onChangePaymentMethodClick() {
    _whenChangePaymentMethodClicked.postValue(Event(true))
  }

  fun onSelectDeliveryTimeClick() {
    _whenSelectDeliveryTimeClicked.postValue(Event(true))
  }

  fun onSelectDeliveryDateClick() {
    _whenSelectDeliveryDateClicked.postValue(Event(true))
  }
  
  fun requireAddresses(): Array<Address> {
    return if (_selectedAddress.value != null) {
      val selectedId = _selectedAddress.value!!.id
      _addresses.value?.map { it.transformSelected(selectedId) }!!.toTypedArray()
    } else {
      emptyArray()
    }
  }
  
  fun requireAddressSectionText(): String {
    return _addressSectionButtonText.value ?: ""
  }

  fun requirePaymentRequest() =
    PaymentRequest(
      type = _paymentMethod.value?.type ?: "",
      bankCode = _paymentMethod.value?.name ?: "",
      walletType = _paymentMethod.value?.name ?: ""
    )

  fun requireCreateOrderRequest() =
    CreateOrderRequest(
      products = _orderProducts.value!!.toProductRequest(),
      addressId = _selectedAddress.value!!.id,
      taxFee = _tax.value!!,
      shipmentFee = _shippingFee.value!!,
      subtotal = _subtotal.value!!,
      total = _totalPrice.value!!,
      deliveryTimeId = _selectedDeliveryTime.value?.id ?: 0,
      deliveryDate = _selectedDeliveryDate.value.toString(),
      paymentTypeId = _paymentMethod.value?.id
    )
  
  fun requireToFetchNewAddress() {
    viewModelScope.launch {
      getAddresses()
    }
  }

  fun requireDeliveryTimeList() = _deliveryTime.value ?: emptyList()

  fun requireDeliveryDateList() = _deliveryDate.value ?: emptyList()
  
  private fun Address.transformSelected(selectedId: Int): Address {
    return if (selectedId == this.id) {
      this.also { it.selectedAddress = true }
    } else {
      this.also { it.selectedAddress = false }
    }
  }
  
  private fun List<Address>.selectPrimary() =
    this.find { it.primary }
  
  private fun List<CartProduct>.toOrderProducts() =
    this.map { OrderProduct(
      id = it.id,
      name = it.name,
      image = it.image,
      unitPerGram = it.unitPerGram,
      qty = it.quantity,
      price = it.price,
      finalPrice = (it.price * it.quantity)
    ) }
  
  private fun List<OrderProduct>.toProductRequest(): List<OrderProductRequest> {
    return this.map { orderProduct ->
      OrderProductRequest(
        id = orderProduct.id,
        name = orderProduct.name,
        imageId = orderProduct.image?.id,
        quantity = orderProduct.qty,
        minimumQuantity = orderProduct.minimumQuantity,
        price = orderProduct.price,
        unitPerGram = orderProduct.unitPerGram,
        finalPrice = orderProduct.finalPrice
      )
    }
  }
  
  companion object {
    private const val FAILED_FETCH_ADDRESS = 1
    private const val FAILED_FETCH_PRODUCTS = 2
    private const val FAILED_FETCH_FEES = 3
    private const val FAILED_FETCH_DELIVERY_TIME = 4
    private const val FAILED_FETCH_DELIVERY_DATE = 5
    
    private const val MINIMUM_FREE_WEIGHT = 3000
  }

}