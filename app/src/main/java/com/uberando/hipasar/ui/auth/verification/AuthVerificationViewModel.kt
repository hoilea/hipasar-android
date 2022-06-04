package com.uberando.hipasar.ui.auth.verification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.AccountRepository
import com.uberando.hipasar.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthVerificationViewModel @Inject constructor(
  private val accountRepository: AccountRepository
) : ViewModel()  {

  private val _whenCheckVerificationButtonClick = MutableLiveData<Boolean>()
  val whenCheckVerificationButtonClick: LiveData<Boolean> get() = _whenCheckVerificationButtonClick

  private val _verifyEmailSuccessful = MutableLiveData<Event<Boolean>>()
  val verifyEmailSuccessful: LiveData<Event<Boolean>> get() = _verifyEmailSuccessful

  fun onCheckVerificationButtonClick() {
    _whenCheckVerificationButtonClick.postValue(true)
  }

  fun requireToVerifyHashCode(hash: String) {
    viewModelScope.launch(Dispatchers.IO) {
      accountRepository.verifyUserNewEmail(hash).let { resource ->
        when (resource) {
          is Resource.Success -> _verifyEmailSuccessful.postValue(Event(true))
          is Resource.Failed -> _verifyEmailSuccessful.postValue(Event(false))
        }
      }
    }
  }

}