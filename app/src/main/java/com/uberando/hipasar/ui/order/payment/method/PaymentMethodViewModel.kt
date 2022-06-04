package com.uberando.hipasar.ui.order.payment.method

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.PaymentRepository
import com.uberando.hipasar.entity.PaymentMethod
import com.uberando.hipasar.entity.PaymentMethodType
import com.uberando.hipasar.exception.PaymentMethodException
import com.uberando.hipasar.ui.BaseViewModel
import com.uberando.hipasar.util.addCod
import com.uberando.hipasar.util.buildPaymentMethodType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaymentMethodViewModel @Inject constructor(
  private val paymentRepository: PaymentRepository
) : BaseViewModel() {

  private val _paymentMethodTypes = MutableLiveData<List<PaymentMethodType>>()
  val paymentMethodTypes: LiveData<List<PaymentMethodType>> get() = _paymentMethodTypes

  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator

  private val _showContentContainer = MutableLiveData<Boolean>()
  val showContentContainer: LiveData<Boolean> get() = _showContentContainer

  private val _showFailedContainer = MutableLiveData<Boolean>()
  val showFailedContainer: LiveData<Boolean> get() = _showFailedContainer

  private val _whenBackButtonClicked = MutableLiveData<Boolean>()
  val whenBackButtonClicked: LiveData<Boolean> get() = _whenBackButtonClicked

  init {
    getPaymentMethodWithIcon()
  }

  fun onBackButtonClick() {
    _whenBackButtonClicked.postValue(true)
  }

  private fun getPaymentMethodWithIcon() {
    viewModelScope.launch {
      doActionWithLoadingIndicator(
        givenContext = Dispatchers.IO,
        propertyTarget = _showLoadingIndicator
      ) {
        paymentRepository.getPaymentMethodWithIcon().let { resource ->
          when (resource) {
            is Resource.Success -> {
              _paymentMethodTypes.postValue(resource.data.buildPaymentMethodType())
              _showContentContainer.postValue(true)
            }
            is Resource.Failed -> {
              _showFailedContainer.postValue(true)
            }
          }
        }
      }
    }
  }

  private fun preparePaymentMethodData() {
    viewModelScope.launch {
      doActionWithLoadingIndicator(
        givenContext = Dispatchers.Default,
        propertyTarget = _showLoadingIndicator
      ) {
        try {
          val paymentType = async(Dispatchers.IO) {
            paymentRepository.getPaymentType().let { resource ->
              when (resource) {
                is Resource.Success -> resource.data
                is Resource.Failed -> throw PaymentMethodException()
              }
            }
          }
          val paymentWalletType = async(Dispatchers.IO) {
            paymentRepository.getPaymentWalletType().let { resource ->
              when (resource) {
                is Resource.Success -> resource.data
                is Resource.Failed -> throw PaymentMethodException()
              }
            }
          }
          val paymentBankCode = async(Dispatchers.IO) {
            paymentRepository.getPaymentBankCode().let { resource ->
              when (resource) {
                is Resource.Success -> resource.data
                is Resource.Failed -> throw PaymentMethodException()
              }
            }
          }
          buildMethodTypes(paymentType.await(), paymentWalletType.await(), paymentBankCode.await()) {
            _paymentMethodTypes.postValue(it)
          }
          _showContentContainer.postValue(true)
        } catch (e: PaymentMethodException) {
          _showFailedContainer.postValue(true)
        }
      }
    }
  }

  private suspend fun buildMethodTypes(
    paymentType: List<String>?,
    paymentWalletType: List<String>?,
    paymentBankCode: List<String>?,
    result: (methodTypes: List<PaymentMethodType>) -> Unit
  ) = withContext(Dispatchers.Default) {
    val paymentMethodTypes = mutableListOf<PaymentMethodType>()
    paymentMethodTypes.addCod()
    paymentType?.forEachIndexed { typeIndex, type ->
      paymentMethodTypes.add(PaymentMethodType.TitleType(type))
      if (typeIndex == 0) {
        paymentBankCode?.forEachIndexed { index, bankCode ->
          paymentMethodTypes.add(
            PaymentMethodType.ContentType(
              PaymentMethod(
                id = index,
                type = type,
                name = bankCode
              )
            )
          )
        }
      } else {
        paymentMethodTypes.add(PaymentMethodType.TitleType(type))
        paymentWalletType?.forEachIndexed { index, walletType ->
          paymentMethodTypes.add(
            PaymentMethodType.ContentType(
              PaymentMethod(
                id = index,
                type = type,
                name = walletType
              )
            )
          )
        }
      }
    }
    result(paymentMethodTypes)
  }

}