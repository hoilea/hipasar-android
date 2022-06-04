package com.uberando.hipasar.ui.main.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.AccountRepository
import com.uberando.hipasar.data.repository.AuthRepository
import com.uberando.hipasar.entity.User
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountViewModel @Inject constructor(
  private val repository: AccountRepository,
  private val authRepository: AuthRepository
) : ViewModel() {

  /**
   * Data holder
   */
  private val _userData = MutableLiveData<User>()
  val userData: LiveData<User> get() = _userData

  /**
   * Visibility states
   */
  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator

  private val _showContentContainer = MutableLiveData<Boolean>()
  val showContentContainer: LiveData<Boolean> get() = _showContentContainer

  private val _showErrorContainer = MutableLiveData<Boolean>()
  val showErrorContainer: LiveData<Boolean> get() = _showErrorContainer

  private val _showNotLoggedInContainer = MutableLiveData<Boolean>()
  val showNotLoggedInContainer: LiveData<Boolean> get() = _showNotLoggedInContainer

  /**
   * Request states
   */
  private val _signOutSuccess = MutableLiveData<Event<Boolean>>()
  val signOutSuccess: LiveData<Event<Boolean>> get() = _signOutSuccess

  /**
   * Action states
   */
  private val _whenLogoutClicked = MutableLiveData<Event<Boolean>>()
  val whenLogoutClicked: LiveData<Event<Boolean>> get() = _whenLogoutClicked

  private val _whenCartClicked = MutableLiveData<Event<Boolean>>()
  val whenCartClicked: LiveData<Event<Boolean>> get() = _whenCartClicked

  private val _whenChatClicked = MutableLiveData<Event<Boolean>>()
  val whenChatClicked: LiveData<Event<Boolean>> get() = _whenChatClicked

  private val _whenAddressClicked = MutableLiveData<Event<Boolean>>()
  val whenAddressClicked: LiveData<Event<Boolean>> get() = _whenAddressClicked

  private val _whenAccountClicked = MutableLiveData<Event<Boolean>>()
  val whenAccountClicked: LiveData<Event<Boolean>> get() = _whenAccountClicked

  private val _whenLoginButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenLoginButtonClicked: LiveData<Event<Boolean>> get() = _whenLoginButtonClicked

  private val _whenPrivacyPolicyContainerClicked = MutableLiveData<Event<Boolean>>()
  val whenPrivacyPolicyContainerClicked: LiveData<Event<Boolean>> get() = _whenPrivacyPolicyContainerClicked

  private val _whenContactUsContainerClicked = MutableLiveData<Event<Boolean>>()
  val whenContactUsContainerClicked: LiveData<Event<Boolean>> get() = _whenContactUsContainerClicked

  private val _requireToRemoveGoogleAccount = MutableLiveData<Event<Boolean>>()
  val requireToRemoveGoogleAccount: LiveData<Event<Boolean>> get() = _requireToRemoveGoogleAccount

  private val _requireToRemoveKakaoAccount = MutableLiveData<Event<Boolean>>()
  val requireToRemoveKakaoAccount: LiveData<Event<Boolean>> get() = _requireToRemoveKakaoAccount

  init {
    requireUserProfile()
  }

  private fun requireUserProfile() {
    viewModelScope.launch(Dispatchers.IO) {
      hideContainers()
      _showLoadingIndicator.postValue(true)
      getUserProfile()
      _showLoadingIndicator.postValue(false)
    }
  }

  private suspend fun getUserProfile() {
    withContext(Dispatchers.IO) {
      _showErrorContainer.postValue(false)
      _showNotLoggedInContainer.postValue(false)
      repository.getUserAccount().let { resource ->
        when (resource) {
          is Resource.Success -> {
            _userData.postValue(resource.data)
            _showContentContainer.postValue(true)
          }
          is Resource.Failed -> {
            when (resource.code) {
              Constants.CODE_UNAUTHORIZED -> {
                _showNotLoggedInContainer.postValue(true)
              }
              else -> {
                _showErrorContainer.postValue(true)
              }
            }
          }
        }
      }
    }
  }

  private fun signOutUser() {
    viewModelScope.launch(Dispatchers.IO) {
      authRepository.signOut().let { resource ->
        when (resource) {
          is Resource.Success -> {
            when (resource.data) {
              Constants.AUTH_METHOD_GOOGLE -> {
                _requireToRemoveGoogleAccount.postValue(Event(true))
              }
              Constants.AUTH_METHOD_KAKAO -> {
                _requireToRemoveKakaoAccount.postValue(Event(true))
              }
              else -> {
                _signOutSuccess.postValue(Event(true))
              }
            }
            showUnauthorizedContainerOnly()
          }
          is Resource.Failed -> _signOutSuccess.postValue(Event(false))
        }
      }
    }
  }
  
  private fun showUnauthorizedContainerOnly() {
    _showContentContainer.postValue(false)
    _showNotLoggedInContainer.postValue(true)
    _showErrorContainer.postValue(false)
  }

  private fun hideContainers() {
    _showContentContainer.postValue(false)
    _showNotLoggedInContainer.postValue(false)
    _showErrorContainer.postValue(false)
  }

  fun quietlyGetProfile() {
    viewModelScope.launch {
      getUserProfile()
    }
  }

  fun requireToSignOut() {
    signOutUser()
  }

  fun requireUserData() = _userData.value

  fun onLoginButtonClick() {
    _whenLoginButtonClicked.postValue(Event(true))
  }

  fun onRetryButtonClick() {
    requireUserProfile()
  }

  fun onCartContainerClick() {
    _whenCartClicked.postValue(Event(true))
  }

  fun onChatContainerClick() {
    _whenChatClicked.postValue(Event(true))
  }

  fun onSignOutContainerClick() {
    _whenLogoutClicked.postValue(Event(true))
  }

  fun onAccountContainerClick() {
    _whenAccountClicked.postValue(Event(true))
  }

  fun onAddressContainerClick() {
    _whenAddressClicked.postValue(Event(true))
  }

  fun onPrivacyPolicyContainerClick() {
    _whenPrivacyPolicyContainerClicked.postValue(Event(true))
  }

  fun onContactUsContainerClick() {
    _whenContactUsContainerClicked.postValue(Event(true))
  }

}