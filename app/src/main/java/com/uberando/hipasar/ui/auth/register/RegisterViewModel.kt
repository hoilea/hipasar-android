package com.uberando.hipasar.ui.auth.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.entity.request.SignUpRequest
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.Event
import com.uberando.hipasar.util.Validator
import com.uberando.hipasar.util.setupPhonePrefix
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterViewModel @Inject constructor() : ViewModel() {

  val name = MutableLiveData<String>()
  val phone = MutableLiveData<String>()
  val email = MutableLiveData<String>()
  val password = MutableLiveData<String>()
  val passwordConfirm = MutableLiveData<String>()
  val termsChecked = MutableLiveData<Boolean>()

  private val _whenLoginButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenLoginButtonClicked: LiveData<Event<Boolean>> get() = _whenLoginButtonClicked

  private val _whenTermsButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenTermsButtonClicked: LiveData<Event<Boolean>> get() = _whenTermsButtonClicked

  private val _requireToRegister = MutableLiveData<Event<Boolean>>()
  val requireToRegister: LiveData<Event<Boolean>> get() = _requireToRegister

  private val _showInvalidInputMessage = MutableLiveData<Event<Boolean>>()
  val showInvalidInputMessage: LiveData<Event<Boolean>> get() = _showInvalidInputMessage

  private val _showTermsNotCheckedMessage = MutableLiveData<Event<Boolean>>()
  val showTermsNotCheckedMessage: LiveData<Event<Boolean>> get() = _showTermsNotCheckedMessage

  private fun beginRegistrationFlow() {
    viewModelScope.launch {
      if (userInputValid() && termsChecked.value == true) {
        _requireToRegister.postValue(Event(true))
      } else if (!userInputValid()) {
        _showInvalidInputMessage.postValue(Event(true))
      } else if (termsChecked.value == false || termsChecked.value == null) {
        _showTermsNotCheckedMessage.postValue(Event(true))
      }
    }
  }

  private fun userInputValid() =
    firstNameValid() &&
      phoneValid() &&
      emailValid() &&
      passwordValid() &&
      passwordConfirmValid() &&
      userPasswordConfirmed()

  private fun firstNameValid() =
    name.value.toString() != "" &&
      name.value.toString().length >= Constants.MINIMUM_NAME_LENGTH

  private fun phoneValid() =
    phone.value.toString() != "null" &&
      phone.value.toString() != "" &&
      Validator.validateInputPhone(phone.value.toString())

  /**
   * Email is optional, if email is not provided by user. Don't validate it, just return true (as valid value).
   */
  private fun emailValid() =
    if (email.value == null || email.value == "") {
      true
    } else {
      email.value.toString() != "" &&
        Patterns.EMAIL_ADDRESS.matcher(email.value.toString()).matches()
    }

  private fun passwordValid() =
    password.value.toString() != "" &&
      password.value.toString().length >= Constants.MINIMUM_PASSWORD_LENGTH

  private fun passwordConfirmValid() =
    passwordConfirm.value.toString() != "" &&
      passwordConfirm.value.toString().length >= Constants.MINIMUM_PASSWORD_LENGTH

  private fun userPasswordConfirmed() =
    password.value.toString() == passwordConfirm.value.toString()

  fun requireSignUpData() =
    SignUpRequest(
      name.value.toString(),
      phone.value.toString().setupPhonePrefix(),
      email.value.toString(),
      password.value.toString()
    )

  fun onRegisterButtonClick() {
    beginRegistrationFlow()
  }

  fun onLoginButtonClick() {
    _whenLoginButtonClicked.postValue(Event(true))
  }

  fun onTermsButtonClick() {
    _whenTermsButtonClicked.postValue(Event(true))
  }

}