package com.uberando.hipasar.ui.main.account.setting.change.password

import androidx.lifecycle.*
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.AccountRepository
import com.uberando.hipasar.entity.request.account.ChangePasswordRequest
import com.uberando.hipasar.entity.request.account.SetPasswordOnlyRequest
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AccountChangePasswordViewModel @Inject constructor(
  private val accountRepository: AccountRepository
) : ViewModel() {

  val oldPassword = MutableLiveData<String>()
  val newPassword = MutableLiveData<String>()
  val newPasswordConfirm = MutableLiveData<String>()

  private val _modifyPasswordSuccessful = MutableLiveData<Event<Boolean>>()
  val modifyPasswordSuccessful: LiveData<Event<Boolean>> get() = _modifyPasswordSuccessful

  private val _showLoadingIndicator = MutableLiveData(false)
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator

  val buttonEnabled = Transformations.map(_showLoadingIndicator) { !it }

  private suspend fun doActionWithLoadingIndicator(givenContext: CoroutineContext = Dispatchers.Default, action: suspend () -> Unit) {
    withContext(givenContext) {
      _showLoadingIndicator.postValue(true)
      action()
      _showLoadingIndicator.postValue(false)
    }
  }

  private fun beginSetPasswordFlow() {
    viewModelScope.launch {
      if (setPasswordInputValid()) {
        doActionWithLoadingIndicator(Dispatchers.IO) {
          accountRepository.setUserAccountPassword(
            SetPasswordOnlyRequest(newPassword.value.toString())
          ).let { resource ->
            when (resource) {
              is Resource.Success -> _modifyPasswordSuccessful.postValue(Event(true))
              is Resource.Failed -> _modifyPasswordSuccessful.postValue(Event(false))
            }
          }
        }
      }
    }
  }

  private fun beginChangePasswordFlow() {
    viewModelScope.launch {
      if (changePasswordInputValid()) {
        doActionWithLoadingIndicator(Dispatchers.IO) {
          accountRepository.changeUserAccountPassword(
            ChangePasswordRequest(oldPassword.value.toString(), newPassword.value.toString())
          ).let { resource ->
            when (resource) {
              is Resource.Success -> _modifyPasswordSuccessful.postValue(Event(true))
              is Resource.Failed -> _modifyPasswordSuccessful.postValue(Event(false))
            }
          }
        }
      }
    }
  }

  private fun setPasswordInputValid() =
    newPasswordInputValid() &&
      newPasswordConfirmInputValid()

  private fun changePasswordInputValid() =
    oldPasswordInputValid() &&
      newPasswordInputValid() &&
      newPasswordConfirmInputValid()

  private fun oldPasswordInputValid() =
    oldPassword.value.toString() != "null" &&
      oldPassword.value.toString() != "" &&
      oldPassword.value.toString().length >= Constants.MINIMUM_PASSWORD_LENGTH &&
      Regex(Constants.PATTERN_ALPHANUMERIC).matches(oldPassword.value.toString())

  private fun newPasswordInputValid() =
    newPassword.value.toString() != "null" &&
      newPassword.value.toString() != "" &&
      newPassword.value.toString().length >= Constants.MINIMUM_PASSWORD_LENGTH &&
      Regex(Constants.PATTERN_ALPHANUMERIC).matches(newPassword.value.toString())

  private fun newPasswordConfirmInputValid() =
    newPasswordConfirm.value.toString() == newPassword.value.toString()

  fun onSetButtonClick() {
    beginSetPasswordFlow()
  }

  fun onChangeButtonClick() {
    beginChangePasswordFlow()
  }

}