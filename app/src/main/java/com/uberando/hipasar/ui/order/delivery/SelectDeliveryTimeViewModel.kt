package com.uberando.hipasar.ui.order.delivery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uberando.hipasar.ui.BaseViewModel
import javax.inject.Inject

class SelectDeliveryTimeViewModel @Inject constructor() : BaseViewModel() {

  private val _whenNavigationIconClicked = MutableLiveData<Boolean>()
  val whenNavigationIconClicked: LiveData<Boolean> get() = _whenNavigationIconClicked

  fun onNavigationIconClick() {
    _whenNavigationIconClicked.postValue(true)
  }

}