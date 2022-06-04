package com.uberando.hipasar.ui.order.payment.confirm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.PaymentRepository
import com.uberando.hipasar.entity.request.PaymentRequest
import com.uberando.hipasar.ui.BaseViewModel
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.Event
import com.uberando.hipasar.util.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PaymentConfirmViewModel @Inject constructor() : BaseViewModel() {

  private var _paymentRequest = PaymentRequest()

  val phone = MutableLiveData<String>()

  val accountOwnerName = MutableLiveData<String>()

  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator

  private val _showInvalidInputMessage = MutableLiveData<Event<Boolean>>()
  val showInvalidInputMessage: LiveData<Event<Boolean>> get() = _showInvalidInputMessage

  val eWalletButtonEnabled: LiveData<Boolean> = Transformations.map(phone) {
    Validator.validateInputPhone(it)
  }

  val vaConfirmButtonEnabled: LiveData<Boolean> = Transformations.map(accountOwnerName) {
    Validator.validateInputName(it)
  }

  private val _whenCloseButtonClicked = MutableLiveData<Event<Boolean>>()
  val whenCloseButtonClicked: LiveData<Event<Boolean>> get() = _whenCloseButtonClicked

  private val _whenOrderDataAccepted = MutableLiveData<Event<Boolean>>()
  val whenOrderDataAccepted: LiveData<Event<Boolean>> get() = _whenOrderDataAccepted

  fun setupPaymentRequest(paymentRequest: PaymentRequest) {
    _paymentRequest = paymentRequest
  }

  fun onCloseButtonClick() {
    _whenCloseButtonClicked.postValue(Event(true))
  }

  fun onActionButtonClick() {
    validateInputFirst { _whenOrderDataAccepted.postValue(Event(true)) }
  }

  /**
   * Validate an appropriate property based on payment type whether virtual account or e-wallet.
   */
  private fun validateInputFirst(nextAction: () -> Unit) {
    val inputValid =
      when (_paymentRequest.type) {
        Constants.PAYMENT_TYPE_VA -> {
          Validator.validateInputName(accountOwnerName.value.toString())
        }
        Constants.PAYMENT_TYPE_EWALLET -> {
          Validator.validateInputPhone(phone.value.toString())
        }
        else -> {
          false
        }
      }
    if (inputValid) {
      nextAction()
    } else {
      _showInvalidInputMessage.postValue(Event(true))
    }
  }

  fun requirePaymentRequest() =
    _paymentRequest.also {
      it.accountName = accountOwnerName.value.toString()
      it.mobileNumber = phone.value.toString()
    }

}