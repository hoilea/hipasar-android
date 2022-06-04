package com.uberando.hipasar.ui.auth.recovery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.ui.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class PasswordRecoveryViewModel @Inject constructor() : BaseViewModel() {

  val email = MutableLiveData<String>()

  private val _isLoading = MutableLiveData(false)
  val isLoading: LiveData<Boolean> get() = _isLoading

  val buttonEnabled = Transformations.map(_isLoading) { !it }

  private val _whenBackButtonClicked = MutableLiveData<Boolean>()
  val whenBackButtonClicked: LiveData<Boolean> get() = _whenBackButtonClicked

  fun onBackButtonClick() {
    _whenBackButtonClicked.postValue(true)
  }

  fun onActionButtonClick() {
    viewModelScope.launch {
      _isLoading.postValue(true)
      delay(2000L)
      _isLoading.postValue(false)
    }
  }

}