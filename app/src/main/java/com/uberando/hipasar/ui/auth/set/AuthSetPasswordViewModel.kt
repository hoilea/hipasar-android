package com.uberando.hipasar.ui.auth.set

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.Event
import javax.inject.Inject

class AuthSetPasswordViewModel @Inject constructor() : ViewModel() {

  val password = MutableLiveData<String>()
  val passwordConfirm = MutableLiveData<String>()

  private val _whenRequireToSetPassword = MutableLiveData<Event<Boolean>>()
  val whenRequireToSetPassword: LiveData<Event<Boolean>> get() = _whenRequireToSetPassword

  private val _whenSkipButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenSkipButtonClicked: LiveData<Event<Boolean>> get() = _whenSkipButtonClicked

  private fun beginSetPasswordFlow() {
    if (userInputValid()) {
      _whenRequireToSetPassword.postValue(Event(true))
    }
  }

  private fun userInputValid() =
    isPasswordValid() && userPasswordConfirmed()

  private fun isPasswordValid() =
    password.value.toString() != "null" &&
      password.value.toString() != "" &&
      password.value.toString().length >= Constants.MINIMUM_PASSWORD_LENGTH

  private fun userPasswordConfirmed() =
    password.value.toString() == passwordConfirm.value.toString()

  fun onSetButtonClick() {
    beginSetPasswordFlow()
  }

  fun onSkipButtonClick() {
    _whenSkipButtonClicked.postValue(Event(true))
  }

  fun requirePassword() = password.value.toString()

}