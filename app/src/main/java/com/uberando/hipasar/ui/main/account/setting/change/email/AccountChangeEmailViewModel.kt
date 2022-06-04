package com.uberando.hipasar.ui.main.account.setting.change.email

import android.util.Patterns
import androidx.lifecycle.*
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.AccountRepository
import com.uberando.hipasar.entity.User
import com.uberando.hipasar.entity.request.EmailOnlyRequest
import com.uberando.hipasar.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccountChangeEmailViewModel @Inject constructor(
  private val accountRepository: AccountRepository
) : ViewModel() {

  private val _toolbarTitle = MutableLiveData<String>()
  val toolbarTitle: LiveData<String> get() = _toolbarTitle

  private val _buttonText = MutableLiveData<String>()
  val buttonText: LiveData<String> get() = _buttonText

  private val _guideMessage = MutableLiveData<String>()
  val guideMessage: LiveData<String> get() = _guideMessage

  private val _user = MutableLiveData<User>()
  val user: LiveData<User> get() = _user

  val email = MutableLiveData<String>()
  val buttonEnabled = Transformations.map(email) {
    it != _user.value?.email && !_isLoading.value!!
  }
  private val _isLoading = MutableLiveData(false)
  val isLoading: LiveData<Boolean> get() = _isLoading

  private val _whenBackButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenBackButtonClicked: LiveData<Event<Boolean>> get() = _whenBackButtonClicked

  private val _whenUpdateEmailSuccess = MutableLiveData<Event<Boolean>>()
  val whenUpdateEmailSuccess: LiveData<Event<Boolean>> get() = _whenUpdateEmailSuccess

  fun setupUser(user: User) {
    _user.postValue(user)
    email.postValue(user.email)
  }

  fun setupToolbarTitle(title: String) {
    _toolbarTitle.postValue(title)
  }

  fun setupButtonText(text: String) {
    _buttonText.postValue(text)
  }

  fun setupGuideMessage(message: String) {
    _guideMessage.postValue(message)
  }

  fun onBackButtonClick() {
    _whenBackButtonClicked.postValue(Event(true))
  }

  fun onActionButtonClick() {
    beginAction()
  }

  private fun beginAction() {
    viewModelScope.launch(Dispatchers.IO) {
      if (emailValid()) {
        _isLoading.postValue(true)
        accountRepository.changeUserEmail(
          EmailOnlyRequest(email.value.toString())
        ).let { resource ->
          when (resource) {
            is Resource.Success -> _whenUpdateEmailSuccess.postValue(Event(true))
            is Resource.Failed -> _whenUpdateEmailSuccess.postValue(Event(false))
          }
        }
        _isLoading.postValue(false)
      }
    }
  }

  private fun emailValid() =
    email.value.toString() != "" &&
      Patterns.EMAIL_ADDRESS.matcher(email.value.toString()).matches()

}