package com.uberando.hipasar.ui.auth

import androidx.lifecycle.*
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.AuthRepository
import com.uberando.hipasar.data.repository.MessagingRepository
import com.uberando.hipasar.entity.request.*
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.Event
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@ActivityScoped
class AuthViewModel @Inject constructor(
  private val repository: AuthRepository,
  private val messagingRepository: MessagingRepository
) : ViewModel() {

  /**
   * Data Holder
   */
  private var _email: String? = null
  private var _visitingContext: String = "authentication"
  private var _hash: String? = null
  private var _phoneNumber = ""

  /**
   * Request states
   */
  private val _signUpSuccessful = MutableLiveData<Event<Boolean>>()
  val signUpSuccessful: LiveData<Event<Boolean>> get() = _signUpSuccessful

  private val _signInBasicSuccessful = MutableLiveData<Event<Boolean>>()
  val signInBasicSuccessful: LiveData<Event<Boolean>> get() = _signInBasicSuccessful

  private val _signInOAuthSuccessful = MutableLiveData<Event<Boolean>>()
  val signInOAuthSuccessful: LiveData<Event<Boolean>> get() = _signInOAuthSuccessful

  private val _setPasswordSuccessful = MutableLiveData<Event<Boolean>>()
  val setPasswordSuccessful: LiveData<Event<Boolean>> get() = _setPasswordSuccessful

  private val _whenSetPasswordCancelled = MutableLiveData<Event<Boolean>>()
  val whenSetPasswordCancelled: LiveData<Event<Boolean>> get() = _whenSetPasswordCancelled

  private val _oAuthPasswordAvailable = MutableLiveData<Boolean>()

  private val _verifyEmailSuccessful = MutableLiveData<Event<Boolean>>()
  val verifyEmailSuccessful: LiveData<Event<Boolean>> get() = _verifyEmailSuccessful

  private val _compareVerificationCodeSuccessful = MutableLiveData<Event<Boolean>>()
  val compareVerificationCodeSuccessful: LiveData<Event<Boolean>> get() = _compareVerificationCodeSuccessful

  /**
   * Result messages
   */
  private val _authResult = MutableLiveData<String>()
  val authResult: LiveData<String> get() = _authResult

  private val _authResultMessage = MutableLiveData<String>()
  val authResultMessage: LiveData<String> get() = _authResultMessage

  /**
   * Visibility states
   */
  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator

  val showAuthResultContainer = Transformations.map(_authResult) { result ->
    result != null
  }

  private val _showSetPasswordFailedContainer = MutableLiveData<Boolean>()
  val showSetPasswordFailedContainer: LiveData<Boolean> get() = _showSetPasswordFailedContainer

  private val _showVerifyEmailFailedContainer = MutableLiveData<Boolean>()
  val showVerifyEmailFailedContainer: LiveData<Boolean> get() = _showVerifyEmailFailedContainer

  /**
   * Action states
   */
  private val _whenRetryButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenRetryButtonClicked: LiveData<Event<Boolean>> get() = _whenRetryButtonClicked

  private val _whenCancelButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenCancelButtonClicked: LiveData<Event<Boolean>> get() = _whenCancelButtonClicked

  private val _whenSetPasswordRetryButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenSetPasswordRetryButtonClicked: LiveData<Event<Boolean>> get() = _whenSetPasswordRetryButtonClicked

  private val _whenRequireToShowResult = MutableLiveData<Event<Boolean>>()
  val whenRequireToShowResult: LiveData<Event<Boolean>> get() = _whenRequireToShowResult

  private val _whenUserPhoneNotVerified = MutableLiveData<Event<Boolean>>()
  val whenUserPhoneNotVerified: LiveData<Event<Boolean>> get() = _whenUserPhoneNotVerified

  private suspend fun doActionWithLoadingIndicator(givenContext: CoroutineContext = Dispatchers.Default, action: suspend () -> Unit) {
    withContext(givenContext) {
      _showLoadingIndicator.postValue(true)
      action()
      _showLoadingIndicator.postValue(false)
    }
  }

  fun tryToPostSignInBasicData(email: String, password: String) {
    viewModelScope.launch {
      doActionWithLoadingIndicator(Dispatchers.IO) {
        _phoneNumber = email
        _visitingContext = Constants.AUTH_VISITING_CONTEXT_SIGN_IN
        repository.signIn(SignInRequest(email, password)).let { resource ->
          when (resource) {
            is Resource.Success -> _signInBasicSuccessful.postValue(Event(true))
            is Resource.Failed -> {
              when (resource.code) {
                Constants.CODE_PHONE_NOT_VERIFIED -> {
                  _whenUserPhoneNotVerified.postValue(Event(true))
                }
                else -> {
                  if (resource.code == Constants.CODE_UNAUTHORIZED) {
                    _authResultMessage.postValue("Nomor telepon atau password salah")
                  } else {
                    _authResultMessage.postValue(resource.message)
                  }
                  _authResult.postValue(Constants.CALLBACK_FAILED)
                }
              }
            }
          }
        }
      }
    }
  }

  fun tryToPostSignInOAuthData(data: SignInOAuthRequest) {
    viewModelScope.launch {
      doActionWithLoadingIndicator(Dispatchers.IO) {
        _email = data.email
        _visitingContext = Constants.AUTH_VISITING_CONTEXT_SIGN_IN
        repository.signInOAuth(data).let { resource ->
          when (resource) {
            is Resource.Success -> {
              _oAuthPasswordAvailable.postValue(resource.data)
              _signInOAuthSuccessful.postValue(Event(true))
            }
            is Resource.Failed -> {
              _authResult.postValue(Constants.CALLBACK_FAILED)
              _authResultMessage.postValue(resource.message)
            }
          }
        }
      }
    }
  }

  fun tryToPostSignUpData(data: SignUpRequest) {
    viewModelScope.launch {
      doActionWithLoadingIndicator(Dispatchers.IO) {
        _email = data.email
        _phoneNumber= data.phone
        _visitingContext = Constants.AUTH_VISITING_CONTEXT_REGISTER
        repository.signUp(data).let { resource ->
          when (resource) {
            is Resource.Success -> _signUpSuccessful.postValue(Event(true))
            is Resource.Failed -> {
              Timber.i("resource: $resource")
              _authResult.postValue(Constants.CALLBACK_FAILED)
              if (resource.message == Constants.CODE_DUPLICATE_ENTRY) {
                _authResultMessage.postValue("Nomor telepon telah digunakan, silahkan login menggunakan nomor tersebut jika memang itu milik anda.")
              } else {
                _authResultMessage.postValue(resource.message)
              }
            }
          }
        }
      }
    }
  }

  fun tryToVerifyEmail(hash: String) {
    viewModelScope.launch {
      doActionWithLoadingIndicator(Dispatchers.IO) {
        _hash = hash
        _visitingContext = Constants.AUTH_VISITING_CONTEXT_VERIFY_EMAIL
        _whenRequireToShowResult.postValue(Event(true))
        requireToClearAuthResult()
        repository.verifyEmail(hash).let { resource ->
          when (resource) {
            is Resource.Success -> _verifyEmailSuccessful.postValue(Event(true))
            is Resource.Failed -> {
//              _showVerifyEmailFailedContainer.postValue(true)
              _authResult.postValue(Constants.CALLBACK_FAILED)
              _authResultMessage.postValue(resource.message)
            }
          }
        }
      }
    }
  }

  fun tryToCheckEmailVerificationStatus() {
    viewModelScope.launch {
      doActionWithLoadingIndicator(Dispatchers.IO) {
        repository.checkEmailVerification(EmailOnlyRequest(_email ?: "")).let { resource ->
          when (resource) {
            is Resource.Success -> _verifyEmailSuccessful.postValue(Event(true))
            is Resource.Failed -> _verifyEmailSuccessful.postValue(Event(false))
          }
        }
      }
    }
  }

  fun tryToPostSetPasswordData(password: String) {
    viewModelScope.launch {
      doActionWithLoadingIndicator(Dispatchers.IO) {
        repository.setPassword(SetPasswordRequest(emailOrEmpty(), password)).let { resource ->
          when (resource) {
            is Resource.Success -> _setPasswordSuccessful.postValue(Event(true))
            is Resource.Failed -> _showSetPasswordFailedContainer.postValue(true)
          }
        }
      }
    }
  }

  fun tryToPostPhoneVerificationCode(verificationCode: String) {
    viewModelScope.launch {
      doActionWithLoadingIndicator(Dispatchers.IO) {
        repository.compareCodeVerification(CompareVerificationCodeRequest(verificationCode)).let { resource ->
          when (resource) {
            is Resource.Success -> {
              _compareVerificationCodeSuccessful.postValue(Event(true))
            }
            is Resource.Failed -> {
              when (resource.code) {
                Constants.CODE_VERIFICATION_NOT_FOUND -> _compareVerificationCodeSuccessful.postValue(Event(false))
                Constants.CODE_VERIFICATION_EXPIRED -> _compareVerificationCodeSuccessful.postValue(Event(false))
                else -> _compareVerificationCodeSuccessful.postValue(Event(false))
              }
            }
          }
        }
      }
    }
  }

  private fun emailOrEmpty() = _email ?: ""

  fun visitingContext() = _visitingContext

  fun requireToCancelSetPassword() {
    _whenSetPasswordCancelled.postValue(Event(true))
  }

  fun requireToClearAuthResult() {
    _authResult.postValue(null)
    _showSetPasswordFailedContainer.postValue(false)
  }

  fun requireToRetryEmailVerification() {
    tryToVerifyEmail(_hash ?: "")
  }

  fun requireToSaveFirebaseToken(token: String) {
    viewModelScope.launch {
      messagingRepository.updateMessagingToken(token)
    }
  }

  fun requireCachedPhoneNumber() = _phoneNumber

  fun oAuthPasswordAvailable() = _oAuthPasswordAvailable.value ?: false

  fun onAuthRetryAction() {
    _whenRetryButtonClicked.postValue(Event(true))
  }

  fun onAuthCancelAction() {
    _whenCancelButtonClicked.postValue(Event(true))
  }

  fun onSetPasswordRetryAction() {
    _whenSetPasswordRetryButtonClicked.postValue(Event(true))
  }

  fun onVerifyEmailRetryAction() {
    Timber.i("require to retry")
  }

  fun onVerifyEmailCancelAction() {
    Timber.i("require to cancel")
  }

}