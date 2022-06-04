package com.uberando.hipasar.ui.main.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.AddressRepository
import com.uberando.hipasar.entity.Address
import com.uberando.hipasar.entity.AddressType
import com.uberando.hipasar.entity.request.address.CreateAddressRequest
import com.uberando.hipasar.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AddressViewModel @Inject constructor(
  private val addressRepository: AddressRepository
) : ViewModel() {

  /**
   * Data Holder
   */
  private val _addressTypes = MutableLiveData<List<AddressType>>()
  val addressTypes: LiveData<List<AddressType>> get() = _addressTypes

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

  /**
   * Action states
   */
  private val _whenBackButtonClicked = MutableLiveData<Boolean>()
  val whenBackButtonClicked: LiveData<Boolean> get() = _whenBackButtonClicked

  private val _whenCreateButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenCreateButtonClicked: LiveData<Event<Boolean>> get() = _whenCreateButtonClicked
  
  /**
   * Request states
   */
  private val _updateAddressSuccessful = MutableLiveData<Event<Boolean>>()
  val updateAddressSuccessful: LiveData<Event<Boolean>> get() = _updateAddressSuccessful
  
  private val _removeAddressSuccessful = MutableLiveData<Event<Boolean>>()
  val removeAddressSuccessful: LiveData<Event<Boolean>> get() = _removeAddressSuccessful
  
  init {
    getAddresses()
  }

  private suspend fun doActionWithLoadingIndicator(givenContext: CoroutineContext = Dispatchers.Default, action: suspend () -> Unit) {
    withContext(givenContext) {
      hideContainers()
      _showLoadingIndicator.postValue(true)
      action()
      _showLoadingIndicator.postValue(false)
    }
  }

  private fun hideContainers() {
    _showContentContainer.postValue(false)
    _showEmptyContainer.postValue(false)
    _showErrorContainer.postValue(false)
  }

  private fun getAddresses() {
    viewModelScope.launch {
      doActionWithLoadingIndicator(Dispatchers.IO) {
        addressRepository.getAddresses().let { resource ->
          when (resource) {
            is Resource.Success -> {
              if (resource.data != null && resource.data.isNotEmpty()) {
                _addressTypes.postValue(resource.data.toAddressTypes())
                _showContentContainer.postValue(true)
              } else {
                _showEmptyContainer.postValue(true)
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
  
  private fun updateAddress(addressId: Int, request: CreateAddressRequest) {
    viewModelScope.launch(Dispatchers.IO) {
      addressRepository.updateAddress(addressId, request).let { resource ->
        when (resource) {
          is Resource.Success -> {
            getAddresses()
            _updateAddressSuccessful.postValue(Event(true))
          }
          is Resource.Failed -> _updateAddressSuccessful.postValue(Event(false))
        }
      }
    }
  }
  
  private fun removeAddress(addressId: Int) {
    viewModelScope.launch(Dispatchers.IO) {
      addressRepository.removeAddress(addressId).let { resource ->
        when (resource) {
          is Resource.Success -> {
            getAddresses()
            _removeAddressSuccessful.postValue(Event(true))
          }
          is Resource.Failed -> _removeAddressSuccessful.postValue(Event(false))
        }
      }
    }
  }

  fun onNavigationIconClick() {
    _whenBackButtonClicked.postValue(true)
  }

  fun onRetryButtonClick() {
    getAddresses()
  }

  fun onCreateButtonClick() {
    _whenCreateButtonClicked.postValue(Event(true))
  }
  
  fun requireToRefreshAddress() {
    getAddresses()
  }
  
  fun requireToSetPrimaryAddress(address: Address) {
    updateAddress(address.id, address.also { it.primary = true }.makeAddressRequest())
  }
  
  fun requireToRemoveAddress(addressId: Int) {
    removeAddress(addressId)
  }

  private fun List<Address>.toAddressTypes(): List<AddressType> {
    return this.map { address ->
      if (address.primary) {
        AddressType.PrimaryType(address)
      } else {
        AddressType.NonPrimaryType(address)
      }
    }
  }
  
  private fun Address.makeAddressRequest() =
    CreateAddressRequest(
      this.basicAddress,
      this.detailAddress,
      this.postCode,
      this.reference,
      this.phone,
      this.receiverName,
      this.primary
    )

}