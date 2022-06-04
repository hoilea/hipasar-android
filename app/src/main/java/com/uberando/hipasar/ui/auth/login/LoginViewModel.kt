package com.uberando.hipasar.ui.auth.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.Event
import com.uberando.hipasar.util.Validator
import com.uberando.hipasar.util.setupPhonePrefix
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor() : ViewModel() {

  val phone = MutableLiveData<String>()
  val password = MutableLiveData<String>()
  val termsChecked = MutableLiveData(false)

  private val _requireToSignIn = MutableLiveData<Event<Boolean>>()
  val requireToSignIn: LiveData<Event<Boolean>> get() = _requireToSignIn

  /**
   * Visibility states
   */
  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator

  private val _showInvalidInputMessage = MutableLiveData<Event<Boolean>>()
  val showInvalidInputMessage: LiveData<Event<Boolean>> get() = _showInvalidInputMessage

  private val _showTermsNotCheckedMessage = MutableLiveData<Event<Boolean>>()
  val showTermsNotCheckedMessage: LiveData<Event<Boolean>> get() = _showTermsNotCheckedMessage

  /**
   * Action states
   */
  private val _whenRegisterButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenRegisterButtonClicked: LiveData<Event<Boolean>> get() = _whenRegisterButtonClicked

  private val _whenGoogleButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenGoogleButtonClicked: LiveData<Event<Boolean>> get() = _whenGoogleButtonClicked

  private val _whenTermsButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenTermsButtonClicked: LiveData<Event<Boolean>> get() = _whenTermsButtonClicked

  private val _whenForgotPasswordButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenForgotPasswordButtonClicked: LiveData<Event<Boolean>> get() = _whenForgotPasswordButtonClicked

  private fun beginBasicAuthFlow() {
    viewModelScope.launch {
      if (userInputValid() && termsChecked.value == true) {
        _requireToSignIn.postValue(Event(true))
      } else if (!userInputValid()) {
        _showInvalidInputMessage.postValue(Event(true))
      } else if (termsChecked.value == false) {
        _showTermsNotCheckedMessage.postValue(Event(true))
      }
    }
  }

  private fun userInputValid() =
    usernameInputValid() && passwordInputValid()

  private fun usernameInputValid() =
    phone.value.toString() != "null" &&
      phone.value.toString() != "" &&
      Validator.validateInputPhone(phone.value.toString())

  private fun passwordInputValid() =
    password.value.toString() != "null" &&
      password.value.toString() != "" &&
      password.value.toString().length >= Constants.MINIMUM_PASSWORD_LENGTH &&
      Regex(Constants.PATTERN_ALPHANUMERIC).matches(password.value.toString())

  fun requirePhoneNumber() = phone.value.toString().setupPhonePrefix()
  fun requirePassword() = password.value.toString()

  fun requireToShowLoadingIndicator() = _showLoadingIndicator.postValue(true)
  fun requireToHideLoadingIndicator() = _showLoadingIndicator.postValue(false)

  fun onLoginButtonClick() {
    beginBasicAuthFlow()
  }

  fun onGoogleButtonClick() {
    if (termsChecked.value == true) {
      _whenGoogleButtonClicked.postValue(Event(true))
    } else if (termsChecked.value == false) {
      _showTermsNotCheckedMessage.postValue(Event(true))
    }
  }

  fun onRegisterButtonClick() {
    _whenRegisterButtonClicked.postValue(Event(true))
  }

  fun onTermsButtonClick() {
    _whenTermsButtonClicked.postValue(Event(true))
  }

  fun onForgotPasswordButtonClick() {
    _whenForgotPasswordButtonClicked.postValue(Event(true))
  }

}