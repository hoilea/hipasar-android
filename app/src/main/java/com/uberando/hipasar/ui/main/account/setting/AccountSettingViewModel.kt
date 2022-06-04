package com.uberando.hipasar.ui.main.account.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.AccountRepository
import com.uberando.hipasar.data.repository.FileRepository
import com.uberando.hipasar.entity.User
import com.uberando.hipasar.entity.request.account.ChangeDisplayProfileRequest
import com.uberando.hipasar.util.Event
import com.uberando.hipasar.util.updateImageProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class AccountSettingViewModel @Inject constructor(
  private val accountRepository: AccountRepository,
  private val fileRepository: FileRepository
) : ViewModel() {

  /**
   * Data holder
   */
  private val _user = MutableLiveData<User>()
  val user: LiveData<User> get() = _user

  /**
   * Action states
   */
  private val _whenNavigationIconClicked = MutableLiveData<Event<Boolean>>()
  val whenNavigationIconClicked: LiveData<Event<Boolean>> get() = _whenNavigationIconClicked

  private val _whenChangePhotoClicked = MutableLiveData<Event<Boolean>>()
  val whenChangePhotoClicked: LiveData<Event<Boolean>> get() = _whenChangePhotoClicked

  private val _whenChangeNameClicked = MutableLiveData<Event<Boolean>>()
  val whenChangeNameClicked: LiveData<Event<Boolean>> get() = _whenChangeNameClicked

  private val _whenChangeEmailClicked = MutableLiveData<Event<Boolean>>()
  val whenChangeEmailClicked: LiveData<Event<Boolean>> get() = _whenChangeEmailClicked

  private val _whenChangePasswordClicked = MutableLiveData<Event<Boolean>>()
  val whenChangePasswordClicked: LiveData<Event<Boolean>> get() = _whenChangePasswordClicked

  private val _whenUploadImageFailed = MutableLiveData<Event<Boolean>>()
  val whenUploadImageFailed: LiveData<Event<Boolean>> get() = _whenUploadImageFailed

  private val _whenUpdateProfileSuccess = MutableLiveData<Event<Boolean>>()
  val whenUpdateProfileSuccess: LiveData<Event<Boolean>> get() = _whenUpdateProfileSuccess

  /**
   * Set internal property
   */
  fun setUserProperty(user: User) {
    _user.postValue(user)
  }

  /**
   * External requirement action
   */
  fun requireToSetNewPhoto(photo: File) {
    viewModelScope.launch(Dispatchers.IO) {
      if (uploadImageToServer(photo)) {
        if (updateNewProfile()) {
          _whenUpdateProfileSuccess.postValue(Event(true))
        } else {
          _whenUpdateProfileSuccess.postValue(Event(false))
        }
      } else {
        _whenUploadImageFailed.postValue(Event(true))
      }
    }
  }

  fun quietlyUpdateProfile() {
    viewModelScope.launch { getUserProfile() }
  }

  fun requirePasswordAvailable() =
    _user.value?.passwordAvailable ?: false

  /**
   * repository access
   */
  private suspend fun uploadImageToServer(file: File): Boolean {
    return withContext(Dispatchers.IO) {
      fileRepository.uploadImage(file).let { resource ->
        when (resource) {
          is Resource.Success -> {
            _user.updateImageProperty(resource.data)
            true
          }
          is Resource.Failed -> false
        }
      }
    }
  }

  private suspend fun updateNewProfile(): Boolean {
    return withContext(Dispatchers.IO) {
      _user.value?.let { user ->
        accountRepository.changeUserDisplayProfile(
          ChangeDisplayProfileRequest(
            user.name,
            user.image?.id
          )
        ).let { resource ->
          when (resource) {
            is Resource.Success -> true
            is Resource.Failed -> false
          }
        }
      } ?: false
    }
  }

  private suspend fun getUserProfile() {
    withContext(Dispatchers.IO) {
      accountRepository.getUserAccount().let { resource ->
        when (resource) {
          is Resource.Success -> _user.postValue(resource.data)
          is Resource.Failed -> Unit
        }
      }
    }
  }

  /**
   * Action Trigger
   */
  fun onNavigationIconClick() {
    _whenNavigationIconClicked.postValue(Event(true))
  }

  fun onChangePhotoClick() {
    _whenChangePhotoClicked.postValue(Event(true))
  }

  fun onChangeNameClick() {
    _whenChangeNameClicked.postValue(Event(true))
  }

  fun onChangeEmailClick() {
    _whenChangeEmailClicked.postValue(Event(true))
  }

  fun onChangePasswordClick() {
    _whenChangePasswordClicked.postValue(Event(true))
  }

}