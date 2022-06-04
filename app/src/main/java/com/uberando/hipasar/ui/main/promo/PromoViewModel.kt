package com.uberando.hipasar.ui.main.promo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.ProductRepository
import com.uberando.hipasar.entity.Product
import com.uberando.hipasar.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PromoViewModel @Inject constructor(
  private val productRepository: ProductRepository
) : BaseViewModel() {

  private val _products = MutableLiveData<List<Product>>()
  val products: LiveData<List<Product>> get() = _products

  private val _whenBackButtonClicked = MutableLiveData<Boolean>()
  val whenBackButtonClicked: LiveData<Boolean> get() = _whenBackButtonClicked

  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator

  private val _showContentContainer = MutableLiveData<Boolean>()
  val showContentContainer: LiveData<Boolean> get() = _showContentContainer

  private val _showMessageContainer = MutableLiveData<Boolean>()
  val showMessageContainer: LiveData<Boolean> get() = _showMessageContainer

  init {
    getProducts()
  }

  private fun getProducts() {
    viewModelScope.launch {
      doActionWithLoadingIndicator(
        givenContext = Dispatchers.IO,
        propertyTarget = _showLoadingIndicator,
      ) {
        productRepository.getProductByCategory().let { resource ->
          when (resource) {
            is Resource.Success -> {
              if (resource.data != null && resource.data.isNotEmpty()) {
                _products.postValue(resource.data.first().products)
                _showContentContainer.postValue(true)
              } else {
                _showMessageContainer.postValue(true)
              }
            }
            is Resource.Failed -> {
              _showMessageContainer.postValue(true)
            }
          }
        }
      }
    }
  }

  fun onBackButtonClick() {
    _whenBackButtonClicked.postValue(true)
  }

}