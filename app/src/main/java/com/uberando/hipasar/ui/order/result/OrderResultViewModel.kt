package com.uberando.hipasar.ui.order.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class OrderResultViewModel @Inject constructor() : ViewModel() {
  
  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator
  
  private val _showResultContainer = MutableLiveData<Boolean>()
  val showResultContainer: LiveData<Boolean> get() = _showResultContainer
  
  private val _paymentCallbackCode = MutableLiveData<String>()
  val paymentCallbackCode: LiveData<String> get() = _paymentCallbackCode
  
  private val _whenCloseButtonClicked = MutableLiveData<Boolean>()
  val whenCloseButtonClicked: LiveData<Boolean> get() = _whenCloseButtonClicked
  
  fun setupLoadingState(state: Boolean) {
    _showLoadingIndicator.postValue(state)
  }
  
  fun setupResultContainerVisibilityState(state: Boolean) {
    _showResultContainer.postValue(state)
  }
  
  fun setupResultCode(code: String) {
    _paymentCallbackCode.postValue(code)
  }
  
  fun onCloseButtonClick() {
    _whenCloseButtonClicked.postValue(true)
  }
  
}