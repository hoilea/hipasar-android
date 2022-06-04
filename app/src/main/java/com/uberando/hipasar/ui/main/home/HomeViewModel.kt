package com.uberando.hipasar.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.BannerRepository
import com.uberando.hipasar.data.repository.CartRepository
import com.uberando.hipasar.data.repository.ProductRepository
import com.uberando.hipasar.entity.*
import com.uberando.hipasar.ui.BaseViewModel
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.Event
import com.uberando.hipasar.util.asOnCartProduct
import com.uberando.hipasar.util.valueOrZero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeViewModel @Inject constructor(
  private val productRepository: ProductRepository,
  private val cartRepository: CartRepository,
  private val bannerRepository: BannerRepository
) : BaseViewModel() {

  /**
   * Data holder
   */
  private val _categories = MutableLiveData<List<Category>>()
  val categories: LiveData<List<Category>> get() = _categories

  private val _cartBadgeCount = MutableLiveData(0)
  val cartBadgeCount: LiveData<Int> get() = _cartBadgeCount

  private val _cartSubtotal = MutableLiveData(0)
  val cartSubtotal: LiveData<Int> get() = _cartSubtotal

  private val _cachedPosition = MutableLiveData<Int>()

  private val _cachedProduct = MutableLiveData<Product>()

  private val _cartLoadStatus = MutableSharedFlow<CartLoadStatus>()
  val cartLoadStatus = _cartLoadStatus.asSharedFlow()

  private val _banners = MutableLiveData<List<Banner>>()
  val banners: LiveData<List<Banner>> get() = _banners

  /**
   * Indicate cart product is on progress
   */
  private var _onAddCartProduct = false

  /**
   * Visibility states
   */
  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator

  private val _showContentContainer = MutableLiveData<Boolean>()
  val showContentContainer: LiveData<Boolean> get() = _showContentContainer

  private val _showEmptyContainer = MutableLiveData<Boolean>()
  val showEmptyContainer: LiveData<Boolean> get() = _showEmptyContainer

  private val _showErrorContainer = MutableLiveData<Boolean>()
  val showErrorContainer: LiveData<Boolean> get() = _showErrorContainer

  private val _showCartBadge = MutableLiveData(false)
  val showCartBadge: LiveData<Boolean> get() = _showCartBadge

  private val _showUnauthorizedMessage = MutableLiveData<Event<Boolean>>()
  val showUnauthorizedMessage: LiveData<Event<Boolean>> get() = _showUnauthorizedMessage

  private val _whenCartChanged = MutableLiveData<Boolean>()
  val whenCartChanged: LiveData<Boolean> get() = _whenCartChanged

  private val _whenCartContainerClicked = MutableLiveData<Event<Boolean>>()
  val whenCartContainerClicked: LiveData<Event<Boolean>> get() = _whenCartContainerClicked

  private val _whenSearchBoxClicked = MutableLiveData<Event<Boolean>>()
  val whenSearchBoxClicked: LiveData<Event<Boolean>> get() = _whenSearchBoxClicked

  private val _whenCheckoutButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenCheckoutButtonClicked: LiveData<Event<Boolean>> get() = _whenCheckoutButtonClicked

  private val _whenProductAddedToCart = MutableLiveData<Event<Boolean>>()
  val whenProductAddedToCart: LiveData<Event<Boolean>> get() = _whenProductAddedToCart

  init {
    getBanners()
    getProducts()
  }

  private fun hideContainers() {
    _showContentContainer.postValue(false)
    _showErrorContainer.postValue(false)
    _showErrorContainer.postValue(false)
  }

  private fun getBanners() {
    viewModelScope.launch(Dispatchers.IO) {
      bannerRepository.getBanners().let { resource ->
        when (resource) {
          is Resource.Success -> {
            _banners.postValue(resource.data ?: emptyList())
          }
          is Resource.Failed -> Unit
        }
      }
    }
  }

  private fun getProducts() {
    viewModelScope.launch {
      doActionWithLoadingIndicator(
        givenContext = Dispatchers.IO,
        propertyTarget = _showLoadingIndicator,
        onBeforeAction = { hideContainers() },
      ) {
        productRepository.getProductByCategory().let { resource ->
          when (resource) {
            is Resource.Success -> {
              if (resource.data != null && resource.data.isNotEmpty()) {
                _categories.postValue(resource.data.computeProductOnCart(getCartProducts()))
                _showContentContainer.postValue(true)
              } else {
                _showEmptyContainer.postValue(true)
              }
            }
            is Resource.Failed -> _showErrorContainer.postValue(true)
          }
        }
      }
    }
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
  
  private suspend fun List<Category>.computeProductOnCart(cartProducts: List<CartProduct>): List<Category> {
    return withContext(Dispatchers.Default) {
      this@computeProductOnCart.map { category ->
        category.also { c ->
          c.products.map { product ->
            product.also { p ->
              cartProducts.find { it.id == p.id }.let { cp ->
                if (cp != null) {
                  p.productOnCart = true
                  p.productOnCartAmount = cp.quantity
                }
              }
            }
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
              setCartLoadStatusAdded(_cachedProduct.value?.asOnCartProduct())
              _whenProductAddedToCart.postValue(Event(true))
              getCartProducts()
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
  
  fun requireToRefreshProduct() {
    getProducts()
  }

  fun requireToGetCartBadgeState(): Boolean {
    return _showCartBadge.value ?: false
  }

  fun requireToGetCartBadgeCount(): String {
    return _cartBadgeCount.valueOrZero().toString()
  }

  fun requireCachedProduct(): Product? = _cachedProduct.value

  fun requireCachedPosition(): Int = _cachedPosition.valueOrZero()

  fun requireCategoryList() = _categories.value

  fun onRetryButtonClick() {
    getProducts()
  }

  fun onCheckoutButtonClick() {
    _whenCheckoutButtonClicked.postValue(Event(true))
  }

  fun onCartCardContainerClick() {
    _whenCartContainerClicked.postValue(Event(true))
  }

  fun onSearchBoxClick() {
    _whenSearchBoxClicked.postValue(Event(true))
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