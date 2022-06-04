package com.uberando.hipasar.ui.main.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.CartRepository
import com.uberando.hipasar.entity.Cart
import com.uberando.hipasar.entity.CartProduct
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartViewModel @Inject constructor(
  private val cartRepository: CartRepository
) : ViewModel() {

  /**
   * Data holder
   */
  private val _cartProducts = MutableLiveData<List<CartProduct>>()
  val cartProducts: LiveData<List<CartProduct>> get() = _cartProducts

  private val _computedPrice = MutableLiveData<Int>()
  val computedPrice: LiveData<Int> get() = _computedPrice

  /**
   * Visibility states
   */
  private val _showMainLoadingIndicator = MutableLiveData<Boolean>()
  val showMainLoadingIndicator: LiveData<Boolean> get() = _showMainLoadingIndicator

  private val _showFooterLoadingIndicator = MutableLiveData<Boolean>()
  val showFooterLoadingIndicator: LiveData<Boolean> get() = _showFooterLoadingIndicator

  private val _showContentContainer = MutableLiveData<Boolean>()
  val showContentContainer: LiveData<Boolean> get() = _showContentContainer

  private val _showCartEmptyMessage = MutableLiveData<Boolean>()
  val showCartEmptyMessage: LiveData<Boolean> get() = _showCartEmptyMessage

  private val _showFailedMessage = MutableLiveData<Boolean>()
  val showFailedMessage: LiveData<Boolean> get() = _showFailedMessage

  private val _showUnauthorizedMessage = MutableLiveData<Boolean>()
  val showUnauthorizedMessage: LiveData<Boolean> get() = _showUnauthorizedMessage

  /**
   * Request states
   */
  private val _removeProductSuccess = MutableLiveData<Event<Boolean>>()
  val removeProductSuccess: LiveData<Event<Boolean>> get() = _removeProductSuccess

  private val _updateProductAmountFailed = MutableLiveData<Event<Boolean>>()
  val updateProductAmountFailed: LiveData<Event<Boolean>> get() = _updateProductAmountFailed

  /**
   * Action states
   */
  private val _whenBackButtonClicked = MutableLiveData<Boolean>()
  val whenBackButtonClicked: LiveData<Boolean> get() = _whenBackButtonClicked

  private val _whenLoginButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenLoginButtonClicked: LiveData<Event<Boolean>> get() = _whenLoginButtonClicked

  private val _whenCheckoutButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenCheckoutButtonClicked: LiveData<Event<Boolean>> get() = _whenCheckoutButtonClicked

  init {
    getUserCart()
  }

  private fun getUserCart() {
    viewModelScope.launch(Dispatchers.IO) {
      _showMainLoadingIndicator.postValue(true)
      _showFooterLoadingIndicator.postValue(true)
      cartRepository.getCart().let { resource ->
        when (resource) {
          is Resource.Success -> {
            if (resource.data.isAvailable()) {
              _cartProducts.postValue(resource.data?.products)
              _showContentContainer.postValue(true)
            } else {
              _showCartEmptyMessage.postValue(true)
              _showContentContainer.postValue(false)
            }
          }
          is Resource.Failed -> {
            if (resource.code == Constants.CODE_UNAUTHORIZED) {
              _showUnauthorizedMessage.postValue(true)
            } else {
              _showFailedMessage.postValue(true)
              _showContentContainer.postValue(false)
            }
          }
        }
      }
      _showMainLoadingIndicator.postValue(false)
      _showFooterLoadingIndicator.postValue(false)
    }
  }

  private fun Cart?.isAvailable() =
    this != null && this.products.isNotEmpty()

  private fun List<CartProduct>?.isAvailable() =
    this != null && this.isNotEmpty()

  fun requireToUpdateProductAmountFromCart(productId: Int, newAmount: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      cartRepository.updateProductAmount(productId, newAmount).let { resource ->
        when (resource) {
          is Resource.Success -> updateInternalProductAmount(productId, newAmount)
          is Resource.Failed -> _updateProductAmountFailed.postValue(Event(true))
        }
      }
    }
  }

  fun requireToRemoveProductFromCart(productId: Int) {
    viewModelScope.launch(Dispatchers.Default) {
      _cartProducts.value?.find { it.id == productId }.also { product ->
        if (product != null) {
          withContext(Dispatchers.IO) {
            cartRepository.deleteProduct(productId).let { resource ->
              when (resource) {
                is Resource.Success -> {
                  requireToUpdateProducts(productId)
                  _removeProductSuccess.postValue(Event(true))
                }
                is Resource.Failed -> _removeProductSuccess.postValue(Event(false))
              }
            }
          }
        }
      }
    }
  }

  private suspend fun requireToUpdateProducts(productId: Int) {
    withContext(Dispatchers.Default) {
      val tempProducts = mutableListOf<CartProduct>().also {
        it.addAll(_cartProducts.value ?: emptyList())
      }
      tempProducts.find { it.id == productId }.let { product ->
        if (product != null) {
          tempProducts.remove(product)
        }
      }
      _cartProducts.postValue(tempProducts)
      updateState(tempProducts)
    }
  }

  private fun updateState(products: List<CartProduct>) {
    if (products.isEmpty()) {
      _showCartEmptyMessage.postValue(true)
      _showContentContainer.postValue(false)
    }
  }

  private fun updateInternalProductAmount(productId: Int, newAmount: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      val tempCartProducts = mutableListOf<CartProduct>()
      tempCartProducts.addAll(_cartProducts.value ?: emptyList())
      withContext(Dispatchers.Default) {
        tempCartProducts.find { it.id == productId }.let { product ->
          if (product != null) {
            val productIndex = tempCartProducts.indexOf(product)
            product.quantity = newAmount
            tempCartProducts[productIndex] = product
          }
        }
      }
      _cartProducts.postValue(tempCartProducts)
    }
  }

  fun onBackButtonClick() {
    _whenBackButtonClicked.postValue(true)
  }

  fun onOrderButtonClick() {
    _whenCheckoutButtonClicked.postValue(Event(true))
  }

  fun onCartProductChanged() {
    tryToComputeTotalPrice()
  }

  fun onLoginButtonClick() {
    _whenLoginButtonClicked.postValue(Event(true))
  }

  private fun tryToComputeTotalPrice() {
    viewModelScope.launch(Dispatchers.IO) {
      var tempPrice = 0
      _showFooterLoadingIndicator.postValue(true)
      if (_cartProducts.value.isAvailable()) {
        withContext(Dispatchers.Default) {
          _cartProducts.value?.forEach { product ->
            tempPrice += product.price * product.quantity
          }
        }
      }
      _computedPrice.postValue(tempPrice)
      _showFooterLoadingIndicator.postValue(false)
    }
  }

}