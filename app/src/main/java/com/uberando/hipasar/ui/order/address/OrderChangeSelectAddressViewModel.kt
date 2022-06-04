package com.uberando.hipasar.ui.order.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uberando.hipasar.util.Event
import javax.inject.Inject

class OrderChangeSelectAddressViewModel @Inject constructor(): ViewModel() {
  
  private val _toolbarTitle = MutableLiveData<String>()
  val toolbarTitle: LiveData<String> get() = _toolbarTitle
  
  private val _addressAvailable = MutableLiveData<Boolean>()
  val addressAvailable: LiveData<Boolean> get() = _addressAvailable
  
  private val _whenBackButtonClicked = MutableLiveData<Boolean>()
  val whenBackButtonClicked: LiveData<Boolean> get() = _whenBackButtonClicked
  
  private val _whenCreateAddressClick = MutableLiveData<Event<Boolean>>()
  val whenCreateAddressClick: LiveData<Event<Boolean>> get() = _whenCreateAddressClick
  
  fun setupToolbarTitle(title: String) {
    _toolbarTitle.postValue(title)
  }
  
  fun setupAddressAvailable(available: Boolean) {
    _addressAvailable.postValue(available)
  }
  
  fun onBackButtonClick() {
    _whenBackButtonClicked.postValue(true)
  }
  
  fun onCreateAddressClick() {
    _whenCreateAddressClick.postValue(Event(true))
  }
  
}