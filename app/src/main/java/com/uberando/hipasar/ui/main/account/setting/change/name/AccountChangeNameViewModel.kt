package com.uberando.hipasar.ui.main.account.setting.change.name

import androidx.lifecycle.*
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.AccountRepository
import com.uberando.hipasar.entity.User
import com.uberando.hipasar.entity.request.account.ChangeDisplayProfileRequest
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccountChangeNameViewModel @Inject constructor(
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

  val name = MutableLiveData<String>()
  val buttonEnabled = Transformations.map(name) {
    it != _user.value?.name && !_isLoading
  }
  private var _isLoading: Boolean = false

  private val _whenBackButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenBackButtonClicked: LiveData<Event<Boolean>> get() = _whenBackButtonClicked

  private val _whenUpdateNameSuccess = MutableLiveData<Event<Boolean>>()
  val whenUpdateNameSuccess: LiveData<Event<Boolean>> get() = _whenUpdateNameSuccess

  fun setupUser(user: User) {
    _user.postValue(user)
    name.postValue(user.name)
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
      if (nameValid()) {
        _isLoading = true
        accountRepository.changeUserDisplayProfile(
          ChangeDisplayProfileRequest(
            name.value.toString(),
            _user.value?.image?.id
          )
        ).let { resource ->
          when (resource) {
            is Resource.Success -> _whenUpdateNameSuccess.postValue(Event(true))
            is Resource.Failed -> _whenUpdateNameSuccess.postValue(Event(false))
          }
        }
        _isLoading = false
      }
    }
  }

  private fun nameValid() =
    name.value.toString() != "" &&
      name.value.toString().length >= Constants.MINIMUM_NAME_LENGTH

}