package com.uberando.hipasar.ui.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.CartRepository
import com.uberando.hipasar.data.repository.ProductRepository
import com.uberando.hipasar.entity.*
import com.uberando.hipasar.ui.BaseViewModel
import com.uberando.hipasar.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchResultViewModel @Inject constructor(
  private val productRepository: ProductRepository,
  private val cartRepository: CartRepository
) : BaseViewModel() {

  private val _searchBoxValue = MutableLiveData<String>()
  val searchBoxValue: LiveData<String> get() = _searchBoxValue

  private val _searchResult = MutableLiveData<List<ProductType>>()
  val searchResult: LiveData<List<ProductType>> get() = _searchResult

  private val _cartBadgeCount = MutableLiveData(0)
  val cartBadgeCount: LiveData<Int> get() = _cartBadgeCount

  private val _cartSubtotal = MutableLiveData(0)
  val cartSubtotal: LiveData<Int> get() = _cartSubtotal

  private val _cachedPosition = MutableLiveData<Int>()

  private val _cachedProduct = MutableLiveData<Product>()

  private val _cartLoadStatus = MutableSharedFlow<CartLoadStatus>()
  val cartLoadStatus = _cartLoadStatus.asSharedFlow()

  /**
   * Indicate cart product is on progress
   */
  private var _onAddCartProduct = false

  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator

  private val _showResultContainer = MutableLiveData<Boolean>()
  val showResultContainer: LiveData<Boolean> get() = _showResultContainer

  private val _showNoResultContainer = MutableLiveData<Boolean>()
  val showNoResultContainer: LiveData<Boolean> get() = _showNoResultContainer

  private val _showErrorContainer = MutableLiveData<Boolean>()
  val showErrorContainer: LiveData<Boolean> get() = _showErrorContainer

  private val _showCartBadge = MutableLiveData(false)
  val showCartBadge: LiveData<Boolean> get() = _showCartBadge

  private val _showUnauthorizedMessage = MutableLiveData<Event<Boolean>>()
  val showUnauthorizedMessage: LiveData<Event<Boolean>> get() = _showUnauthorizedMessage

  private val _whenBackButtonClicked = MutableLiveData<Boolean>()
  val whenBackButtonClicked: LiveData<Boolean> get() = _whenBackButtonClicked

  private val _whenSearchBoxClicked = MutableLiveData<Event<Boolean>>()
  val whenSearchBoxClicked: LiveData<Event<Boolean>> get() = _whenSearchBoxClicked

  private val _whenCartChanged = MutableLiveData<Boolean>()
  val whenCartChanged: LiveData<Boolean> get() = _whenCartChanged

  private val _whenProductAddedToCart = MutableLiveData<Event<Boolean>>()
  val whenProductAddedToCart: LiveData<Event<Boolean>> get() = _whenProductAddedToCart

  fun setupSearchBoxValue(value: String) {
    _searchBoxValue.postValue(value)
    beginSearchFlow(value)
  }

  private fun beginSearchFlow(query: String) {
    viewModelScope.launch {
      doActionWithLoadingIndicator(
        givenContext = Dispatchers.IO,
        propertyTarget = _showLoadingIndicator,
        onBeforeAction = { hideAllContainers() }
      ) {
        productRepository.searchProduct(query).let { resource ->
          when (resource) {
            is Resource.Success -> {
              if (resource.data != null && resource.data.isNotEmpty()) {
                _searchResult.postValue(resource.data.computeProductOnCart(getCartProducts()))
                _showResultContainer.postValue(true)
              } else {
                _showNoResultContainer.postValue(true)
              }
            }
            is Resource.Failed -> {
              _showErrorContainer.postValue(true)
            }
          }
        }
      }
    }
  }

  private fun hideAllContainers() {
    _showErrorContainer.postValue(false)
    _showResultContainer.postValue(false)
    _showNoResultContainer.postValue(false)
  }

  private suspend fun getCartProducts(): List<CartProduct> {
    return withContext(Dispatchers.IO) {
      cartRepository.getCart().let { resource ->
        when (resource) {
          is Resource.Success -> {
            val nullableCartProduct = resource.data?.products ?: emptyList()
            computeCartSubtotal(nullableCartProduct)
            _showCartBadge.postValue(nullableCartProduct.isNotEmpty())
            _cartBadgeCount.postValue(nullableCartProduct.size)
            _whenCartChanged.postValue(true)
            nullableCartProduct
          }
          is Resource.Failed -> {
            _showCartBadge.postValue(false)
            _cartBadgeCount.postValue(0)
            _whenCartChanged.postValue(true)
            emptyList()
          }
        }
      }
    }
  }

  private suspend fun computeCartSubtotal(cartProducts: List<CartProduct>) {
    withContext(Dispatchers.Default) {
      if (cartProducts.isNotEmpty()) {
        var subtotalTemp = 0
        cartProducts.forEach { cartProduct ->
          subtotalTemp += (cartProduct.price * cartProduct.quantity)
        }
        _cartSubtotal.postValue(subtotalTemp)
      }
    }
  }

  fun onBackButtonClick() {
    _whenBackButtonClicked.postValue(true)
  }

  fun onSearchBoxClick() {
    _whenSearchBoxClicked.postValue(Event(true))
  }

  fun onRetryButtonClick() {
    beginSearchFlow(_searchBoxValue.toStringOrEmpty())
  }

  fun requireToAddProductToCart(position: Int? = null, product: Product) {
    // Ignore request when another product is added to cart.
    // User must wait until other product to finish added.
    if (!_onAddCartProduct) {
      viewModelScope.launch(Dispatchers.IO) {
        _onAddCartProduct = true
        position?.let { _cachedPosition.postValue(it) }
        _cachedProduct.postValue(product)
        setCartLoadStatusLoading()
        cartRepository.insertProduct(product).let { resource ->
          when (resource) {
            is Resource.Success -> {
              getCartProducts()
              _whenProductAddedToCart.postValue(Event(true))
              setCartLoadStatusAdded(requireCachedProduct()?.asOnCartProduct())
            }
            is Resource.Failed -> {
              when (resource.code) {
                Constants.CODE_UNAUTHORIZED -> _showUnauthorizedMessage.postValue(Event(true))
                else -> Unit
              }
            }
          }
        }
        setCartLoadStatusNotLoading()
        _onAddCartProduct = false
      }
    }
  }

  fun requireToRemoveProductFromCart(productId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      cartRepository.deleteProduct(productId).let { resource ->
        when (resource) {
          is Resource.Success -> getCartProducts()
          is Resource.Failed -> {
             if (resource.code == Constants.CODE_UNAUTHORIZED) {
              _showUnauthorizedMessage.postValue(Event(true))
            }
          }
        }
      }
    }
  }

  fun requireToIncrementProductFromCart(productId: Int, amount: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      cartRepository.updateProductAmount(productId, amount).let { resource ->
        when (resource) {
          is Resource.Success -> getCartProducts()
          is Resource.Failed -> {
             if (resource.code == Constants.CODE_UNAUTHORIZED) {
              _showUnauthorizedMessage.postValue(Event(true))
            }
          }
        }
      }
    }
  }

  fun requireToDecrementProductFromCart(productId: Int, amount: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      cartRepository.updateProductAmount(productId, amount).let { resource ->
        when (resource) {
          is Resource.Success -> getCartProducts()
          is Resource.Failed -> {
             if (resource.code == Constants.CODE_UNAUTHORIZED) {
              _showUnauthorizedMessage.postValue(Event(true))
            }
          }
        }
      }
    }
  }

  fun requireToGetCartBadgeState(): Boolean {
    return _showCartBadge.value ?: false
  }

  fun requireToGetCartBadgeCount(): String {
    return _cartBadgeCount.valueOrZero().toString()
  }

  fun requireCachedPosition(): Int = _cachedPosition.valueOrZero()

  fun requireCachedProduct(): Product? = _cachedProduct.value

  fun requireListWithUpdatedProduct() = _searchResult.value?.map { productType ->
    if (productType is ProductType.ProductAvailableType) {
      val cp = _cachedProduct.value!!
      if (productType.product.id == cp.id) {
        ProductType.ProductAvailableType(productType.product.also { it.productOnCart = true })
      } else {
        productType
      }
    } else {
      productType
    }
  }

  private suspend fun List<ProductType>.computeProductOnCart(cartProducts: List<CartProduct>): List<ProductType> {
    return withContext(Dispatchers.Default) {
      this@computeProductOnCart.map { productType ->
        if (productType is ProductType.ProductAvailableType) {
          productType.also { pt ->
            pt.product.also { product ->
              cartProducts.find { it.id == product.id }.let { cartProduct ->
                if (cartProduct != null) {
                  product.productOnCart = true
                  product.productOnCartAmount = cartProduct.quantity
                }
              }
            }
          }
        } else {
          productType
        }
      }
    }
  }

  private suspend fun setCartLoadStatusLoading() {
    withContext(Dispatchers.Default) {
      _cartLoadStatus.emit(CartLoadStatus.Loading(requireCachedProduct()?.id ?: 0))
    }
  }

  private suspend fun setCartLoadStatusNotLoading() {
    withContext(Dispatchers.Default) {
      _cartLoadStatus.emit(CartLoadStatus.NotLoading(requireCachedProduct()?.id ?: 0))
    }
  }

  private suspend fun setCartLoadStatusAdded(product: Product?) {
    withContext(Dispatchers.Default) {
      _cartLoadStatus.emit(CartLoadStatus.Added(requireCachedProduct()?.id ?: 0, product))
    }
  }

}