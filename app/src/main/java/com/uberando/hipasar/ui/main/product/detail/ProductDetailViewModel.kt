package com.uberando.hipasar.ui.main.product.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.CartRepository
import com.uberando.hipasar.data.repository.ProductRepository
import com.uberando.hipasar.entity.Product
import com.uberando.hipasar.entity.ProductFeedback
import com.uberando.hipasar.ui.BaseViewModel
import com.uberando.hipasar.util.Event
import com.uberando.hipasar.util.valueOrZero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ProductDetailViewModel @Inject constructor(
  private val productRepository: ProductRepository,
  private val cartRepository: CartRepository
) : BaseViewModel() {
  
  private val _product = MutableLiveData<Product>()
  val product: LiveData<Product> get() = _product

  private val _productOnCart = MutableLiveData<Boolean>()

  val showOutOfStockButton = Transformations.map(_product) { product ->
    product.stock < 1
  }

  val showAddCartButton = Transformations.map(_productOnCart) { onCart ->
    !onCart
  }

  val showAmountUpdater = Transformations.map(_productOnCart) { onCart ->
    onCart
  }

  private val _productOnCartAmount = MutableLiveData<Int>()
  val productOnCartAmount: LiveData<Int> get() = _productOnCartAmount
  
  private var _cachedProductId = 0
  
  private val _productFeedback = MutableLiveData<List<ProductFeedback>>()
  val productFeedback: LiveData<List<ProductFeedback>> get() = _productFeedback
  
  val feedbackAvailable = Transformations.map(_productFeedback) {
    it.isNotEmpty()
  }

  private val _cartBadgeCount = MutableLiveData(0)
  
  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator
  
  private val _showErrorContainer = MutableLiveData<Boolean>()
  val showErrorContainer: LiveData<Boolean> get() = _showErrorContainer
  
  private val _showContentContainer = MutableLiveData<Boolean>()
  val showContentContainer: LiveData<Boolean> get() = _showContentContainer

  private val _showOutOfStockMessage = MutableLiveData<Event<Boolean>>()
  val showOutOfStockMessage: LiveData<Event<Boolean>> get() = _showOutOfStockMessage

  private val _whenBackButtonClicked = MutableLiveData<Boolean>()
  val whenBackButtonClicked: LiveData<Boolean> get() = _whenBackButtonClicked

  private val _whenCartChanged = MutableLiveData<Boolean>()
  val whenCartChanged: LiveData<Boolean> get() = _whenCartChanged

  private val _addProductToCartSuccess = MutableLiveData<Event<Boolean>>()
  val addProductToCartSuccess: LiveData<Event<Boolean>> get() = _addProductToCartSuccess
  
  private val _removeProductFromCartSuccess = MutableLiveData<Event<Boolean>>()
  val removeProductFromCart: LiveData<Event<Boolean>> get() = _removeProductFromCartSuccess
  
  private val _updateProductAmountSuccess = MutableLiveData<Event<Boolean>>()
  val updateProductAmountSuccess: LiveData<Event<Boolean>> get() = _updateProductAmountSuccess
  
  fun cacheIdAndGetProduct(productId: Int) {
    _cachedProductId = productId
    getProductDetail()
    getProductOnCart()
  }
  
  private fun getProductDetail() {
    viewModelScope.launch {
      doActionWithLoadingIndicator(
        givenContext = Dispatchers.IO,
        propertyTarget = _showLoadingIndicator,
        onBeforeAction = { hideContainers() }
      ) {
        productRepository.getProductDetail(_cachedProductId).let { resource ->
          when (resource) {
            is Resource.Success -> {
              _product.postValue(resource.data!!)
              _showContentContainer.postValue(true)
            }
            is Resource.Failed -> _showErrorContainer.postValue(true)
          }
        }
      }
    }
  }
  
  private fun getProductOnCart() {
    viewModelScope.launch(Dispatchers.IO) {
      cartRepository.getCart().let { resource ->
        when (resource) {
          is Resource.Success -> {
            if (resource.data != null) {
              resource.data.products.find { it.id == _cachedProductId}.let { cartProduct ->
                _productOnCartAmount.postValue(cartProduct?.quantity)
                _productOnCart.postValue(cartProduct != null)
              }
              _cartBadgeCount.postValue(resource.data.products.size)
            } else {
              _productOnCart.postValue(false)
            }
            _whenCartChanged.postValue(true)
          }
          is Resource.Failed -> _productOnCart.postValue(false)
        }
      }
    }
  }
  
  private fun addProductToCart() {
    viewModelScope.launch(Dispatchers.IO) {
      cartRepository.insertProduct(_product.value!!.also { it.image = it.images.first() }).let { resource ->
        when (resource) {
          is Resource.Success -> {
            _productOnCart.postValue(true)
            _productOnCartAmount.postValue(1)
            _addProductToCartSuccess.postValue(Event(true))
            getProductOnCart()
          }
          is Resource.Failed -> _addProductToCartSuccess.postValue(Event(false))
        }
      }
    }
  }
  
  private fun removeProductFromCart() {
    viewModelScope.launch(Dispatchers.IO) {
      cartRepository.deleteProduct(_cachedProductId).let { resource ->
        when (resource) {
          is Resource.Success -> {
            _productOnCart.postValue(false)
            _productOnCartAmount.postValue(0)
            _removeProductFromCartSuccess.postValue(Event(true))
            getProductOnCart()
          }
          is Resource.Failed -> _removeProductFromCartSuccess.postValue(Event(true))
        }
      }
    }
  }
  
  private suspend fun updateProductAmount(newAmount: Int) {
    withContext(Dispatchers.IO) {
      cartRepository.updateProductAmount(_cachedProductId, newAmount).let { resource ->
        when (resource) {
          is Resource.Success -> {
            _productOnCartAmount.postValue(newAmount)
            _updateProductAmountSuccess.postValue(Event(true))
          }
          is Resource.Failed -> _updateProductAmountSuccess.postValue(Event(false))
        }
      }
    }
  }
  
  private fun increaseProductAmount() {
    viewModelScope.launch {
      if (_product.value?.stock ?: 0 > _productOnCartAmount.valueOrZero()) {
        updateProductAmount(_productOnCartAmount.valueOrZero() + 1)
      } else {
        _showOutOfStockMessage.postValue(Event(true))
      }
    }
  }
  
  private fun decreaseProductAmount() {
    viewModelScope.launch {
      updateProductAmount(_productOnCartAmount.valueOrZero() - 1)
    }
  }
  
  private fun requireToDecreaseAmount() {
    if (_productOnCartAmount.valueOrZero() > 1) {
      decreaseProductAmount()
    } else {
      removeProductFromCart()
    }
  }

  fun requireToGetCartBadgeState(): Boolean {
    return _productOnCart.value ?: false
  }

  fun requireToGetCartBadgeCount(): String {
    return _cartBadgeCount.valueOrZero().toString()
  }
  
  private fun hideContainers() {
    _showContentContainer.postValue(false)
    _showErrorContainer.postValue(false)
  }
  
  fun onBackButtonClick() {
    _whenBackButtonClicked.postValue(true)
  }
  
  fun onAddToCartButtonClick() {
    addProductToCart()
  }
  
  fun onDecreaseAmountButtonClick() {
    requireToDecreaseAmount()
  }
  
  fun onIncreaseAmountButtonClick() {
    increaseProductAmount()
  }
  
  
}