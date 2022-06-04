package com.uberando.hipasar.ui.auth.verification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.AuthRepository
import com.uberando.hipasar.entity.request.SendPhoneVerificationRequest
import com.uberando.hipasar.ui.BaseViewModel
import com.uberando.hipasar.util.Event
import com.uberando.hipasar.util.asHumanReadableTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class AuthPhoneVerificationViewModel @Inject constructor(
  private val authRepository: AuthRepository
) : BaseViewModel() {

  private val _phoneNumber = MutableLiveData<String>()
  val phoneNumber: LiveData<String> get() = _phoneNumber

  private val _codeCharacters = MutableLiveData<String>()
  val codeCharacters: LiveData<String> get() = _codeCharacters

  val codeIsValid = Transformations.map(_codeCharacters) {
    it.length == 6
  }

  private val _phoneVerificationRequestSent = MutableLiveData<Event<Boolean>>()
  val phoneVerificationRequestSent: LiveData<Event<Boolean>> get() = _phoneVerificationRequestSent

  private val _showSendCodeTimer = MutableLiveData<Boolean>()
  val showSendCodeTimer: LiveData<Boolean> get() = _showSendCodeTimer

  /**
   * Remaining time that user can request verification code
   */
  private val _remainingTimeString = MutableLiveData<String>()
  val remainingTimeString: LiveData<String> get() = _remainingTimeString

  private var _remainingTimeInt = 0

  fun setupPhoneNumber(phone: String) {
    _phoneNumber.postValue(phone)
  }

  fun addCharacter(character: Char) {
    if (_codeCharacters.value != null && _codeCharacters.value != "") {
      if (_codeCharacters.value!!.length < 6) {
        _codeCharacters.value.let { codes ->
          _codeCharacters.postValue(codes.plus(character))
        }
      }
    } else {
      _codeCharacters.postValue("$character")
    }
  }

  fun removeCharacter() {
    _codeCharacters.value.let { codes ->
      _codeCharacters.postValue(codes?.dropLast(1))
    }
  }

  fun requireVerificationCode() = _codeCharacters.value!!

  fun onResendButtonClick() {
    viewModelScope.launch(Dispatchers.IO) {
      authRepository.sendPhoneVerification(
        SendPhoneVerificationRequest(_phoneNumber.value.toString())
      ).let { resource ->
        when (resource) {
          is Resource.Success -> {
            _phoneVerificationRequestSent.postValue(Event(true))
            _showSendCodeTimer.postValue(true)
            startTimer()
          }
          is Resource.Failed -> {
            _phoneVerificationRequestSent.postValue(Event(false))
          }
        }
      }
    }
  }

  private fun startTimer(remainingTime: Int? = null) {
    viewModelScope.launch {
      _remainingTimeInt = if (remainingTime == null) {
        RESENT_CODE_TIME
      } else {
        remainingTime - 1
      }
      _remainingTimeString.postValue(_remainingTimeInt.asHumanReadableTime())
      delay(1000)
      Timber.i("remaining time: ${_remainingTimeString.value}")
      if (_remainingTimeInt > 0) {
        startTimer(_remainingTimeInt)
      } else {
        _showSendCodeTimer.postValue(false)
      }
    }
  }

  companion object {
    const val RESENT_CODE_TIME = 120
  }

}