package com.uberando.hipasar.ui.address.modify

import androidx.lifecycle.*
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.AddressRepository
import com.uberando.hipasar.entity.Address
import com.uberando.hipasar.entity.Kecamatan
import com.uberando.hipasar.entity.request.address.CreateAddressRequest
import com.uberando.hipasar.ui.BaseViewModel
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.Event
import com.uberando.hipasar.util.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class AddressModifyViewModel @Inject constructor(
  private val addressRepository: AddressRepository
) : BaseViewModel() {

  private val _toolbarTitle = MutableLiveData<String>()
  val toolbarTitle: LiveData<String> get() = _toolbarTitle

  private val _buttonText = MutableLiveData<String>()
  val buttonText: LiveData<String> get() = _buttonText

  private var visitingContext = Constants.VISITING_CONTEXT_CREATE

  private var addressData: Address? = null

  val basicAddress = MutableLiveData<String>()

  val detailAddress = MutableLiveData<String>()

  val phone = MutableLiveData<String>()

  val receiverName = MutableLiveData<String>()

  private val _kecamatanList = MutableLiveData<List<Kecamatan>>()

  private val _selectedKecamatan = MutableLiveData<String>()
  val selectedKecamatan: LiveData<String> get() = _selectedKecamatan

  private val _selectedKecamatanId = MutableLiveData<Long>()

  private val _selectedKelurahan = MutableLiveData<String>()
  val selectedKelurahan: LiveData<String> get() = _selectedKelurahan

  var i = 0

  val kelurahanDropdownEnabled: LiveData<Boolean> = Transformations.map(selectedKecamatan) {
    if (i >= 1) {
      _selectedKelurahan.postValue("")
    }
    i++
    it != "" && it != null
  }
  private val _whenBackButtonClicked = MutableLiveData<Boolean>()
  val whenBackButtonClicked: LiveData<Boolean> get() = _whenBackButtonClicked

  private val _whenGetKecamatanClicked = MutableLiveData<Event<Boolean>>()
  val whenGetKecamatanClicked: LiveData<Event<Boolean>> get() = _whenGetKecamatanClicked

  private val _whenGetKelurahanClicked = MutableLiveData<Event<Boolean>>()
  val whenGetKelurahanClicked: LiveData<Event<Boolean>> get() = _whenGetKelurahanClicked

  private val _showLoadingDialog = MutableLiveData<Boolean>()
  val showLoadingDialog: LiveData<Boolean> get() = _showLoadingDialog

  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator

  private val _showContentContainer = MutableLiveData<Boolean>()
  val showContentContainer: LiveData<Boolean> get() = _showContentContainer

  private val _showErrorContainer = MutableLiveData<Boolean>()
  val showErrorContainer: LiveData<Boolean> get() = _showErrorContainer

  private val _createAddressSuccessful = MutableLiveData<Event<Boolean>>()
  val createAddressSuccessful: LiveData<Event<Boolean>> get() = _createAddressSuccessful

  private val _updateAddressSuccessful = MutableLiveData<Event<Boolean>>()
  val updateAddressSuccessful: LiveData<Event<Boolean>> get() = _updateAddressSuccessful

  private var firstLaunch = true

  /**
   * 0 for get kecamatan and 1 for get kelurahan
   */
  private var getLocationType = MutableLiveData<Int>()

  fun setupToolbarTitle(title: String) {
    _toolbarTitle.postValue(title)
  }

  fun setupButtonText(text: String) {
    _buttonText.postValue(text)
  }

  fun setupAddressData(address: Address) {
    addressData = address
    receiverName.postValue(address.receiverName)
    phone.postValue(address.phone)
    basicAddress.postValue(address.basicAddress)
    detailAddress.postValue(address.detailAddress)
    if (firstLaunch) {
      address.basicAddress.determineKecamatanAndKelurahan { kecamatan, kelurahan ->
        _selectedKecamatan.postValue(kecamatan)
        _selectedKelurahan.postValue(kelurahan)
      }
      getKecamatan()
      firstLaunch = false
    }
  }

  fun setupVisitingContext(visitingContext: Int) {
    this.visitingContext = visitingContext
  }

  fun setupSelectedKecamatan(selectedId: Long, selectedName: String) {
    Timber.i("setup selected kecamatan")
    _selectedKecamatan.postValue(selectedName)
    _selectedKecamatanId.postValue(selectedId)
  }

  fun setupSelectedKelurahan(selectedName: String) {
    Timber.i("setup selected kelurahan")
    _selectedKelurahan.postValue(selectedName)
  }

  fun onNavigationIconClick() {
    _whenBackButtonClicked.postValue(true)
  }

  fun onActionButtonClick() {
    if (visitingContext == Constants.VISITING_CONTEXT_CREATE) {
      validateInputFirst { createAddress() }
    } else if (visitingContext == Constants.VISITING_CONTEXT_MODIFY) {
      validateInputFirst { updateAddress() }
    }
  }

  fun onGetKecamatanClick() {
    getLocationType.postValue(0)
    _whenGetKecamatanClicked.postValue(Event(true))
  }

  fun onGetKelurahanClick() {
    getLocationType.postValue(1)
    _whenGetKelurahanClicked.postValue(Event(true))
  }

  fun requireKecamatanId(): Long {
    val kecamatanId = _kecamatanList.value?.filter { it.name == _selectedKecamatan.value.toString() }
    Timber.i("kecamatan List: ${_kecamatanList.value}")
    Timber.i("selected kecamatan id: $kecamatanId")
    return _selectedKecamatanId.value ?:
    _kecamatanList.value?.find { it.name == _selectedKecamatan.value }?.id ?: 9
  }


  fun requireLocationType() = getLocationType.value ?: 0

  private fun validateInputFirst(nextAction: () -> Unit) {
    val inputValid =
      Validator.validateInputName(receiverName.value.toString()) &&
      Validator.validateInputPhone(phone.value.toString()) &&
      selectedKecamatan.value != null && selectedKecamatan.value.toString() != "" &&
      selectedKelurahan.value != null && selectedKelurahan.value.toString() != ""
    if (inputValid) {
      nextAction()
    }
  }

  private fun getKecamatan() {
    viewModelScope.launch(Dispatchers.IO) {
      addressRepository.getKecamatan().let { resource ->
        when (resource) {
          is Resource.Success -> {
            Timber.i("Get kecamatan success: ${resource.data}")
            _kecamatanList.postValue(resource.data!!)
          }
          is Resource.Failed -> Unit
        }
      }
    }
  }

  private fun createAddress() {
    viewModelScope.launch {
      doActionWithLoadingIndicator(Dispatchers.IO, _showLoadingDialog) {
        addressRepository.createAddress(
          CreateAddressRequest(
            basicAddress = buildBasicAddress(),
            detailAddress = detailAddress.value.toString(),
            phone = phone.value.toString(),
            receiverName = receiverName.value.toString()
          )
        ).let { resource ->
          when (resource) {
            is Resource.Success -> _createAddressSuccessful.postValue(Event(true))
            is Resource.Failed -> _createAddressSuccessful.postValue(Event(false))
          }
        }
      }
    }
  }

  private fun updateAddress() {
    viewModelScope.launch {
      doActionWithLoadingIndicator(Dispatchers.IO, _showLoadingDialog) {
        addressRepository.updateAddress(
          addressData!!.id,
          CreateAddressRequest(
            basicAddress = buildBasicAddress(),
            detailAddress = detailAddress.value.toString(),
            phone = phone.value.toString(),
            receiverName = receiverName.value.toString()
          )
        ).let { resource ->
          when (resource) {
            is Resource.Success -> _updateAddressSuccessful.postValue(Event(true))
            is Resource.Failed -> _updateAddressSuccessful.postValue(Event(false))
          }
        }
      }
    }
  }

  private fun buildBasicAddress(): String {
    return "$DEFAULT_DISTRICT, Kec ${selectedKecamatan.value}, ${selectedKelurahan.value}"
  }

  private fun String.determineKecamatanAndKelurahan(data: (kecamatan: String, kelurahan: String) -> Unit) {
    this.split(", ").let {
      data(it[1].removeRange(0, 4), it[2])
    }
  }

  companion object {
    private const val DEFAULT_DISTRICT = "Kab Subang"
  }

}