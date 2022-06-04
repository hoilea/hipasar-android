package com.uberando.hipasar.ui.address.modify.select

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.AddressRepository
import com.uberando.hipasar.entity.Kecamatan
import com.uberando.hipasar.entity.Kelurahan
import com.uberando.hipasar.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddressSelectLocationViewModel @Inject constructor(
  private val addressRepository: AddressRepository
) : BaseViewModel() {

  private val _toolbarTitle = MutableLiveData<String>()
  val toolbarTitle: LiveData<String> get() = _toolbarTitle

  private val _kecamatanList = MutableLiveData<List<Kecamatan>>()
  val kecamatanList: LiveData<List<Kecamatan>> get() = _kecamatanList

  private val _kelurahanList = MutableLiveData<List<Kelurahan>>()
  val kelurahanList: LiveData<List<Kelurahan>> get() = _kelurahanList

  private val _showLoadingIndicator = MutableLiveData<Boolean>()
  val showLoadingIndicator: LiveData<Boolean> get() = _showLoadingIndicator

  private val _showContentContainer = MutableLiveData<Boolean>()
  val showContentContainer: LiveData<Boolean> get() = _showContentContainer

  private val _showErrorContainer = MutableLiveData<Boolean>()
  val showErrorContainer: LiveData<Boolean> get() = _showErrorContainer

  private val _whenBackButtonClicked = MutableLiveData<Boolean>()
  val whenBackButtonClicked: LiveData<Boolean> get() = _whenBackButtonClicked

  /**
   * Data selection is used to determine what data to get (between kecamatan and kelurahan).
   */
  fun setupDataSelection(kecamatanId: Long) {
    if (kecamatanId != -1L) {
      getKelurahan(kecamatanId)
    } else {
      getKecamatan()
    }
  }

  fun setupToolbarTitle(title: String) {
    _toolbarTitle.postValue(title)
  }

  private fun getKecamatan() {
    viewModelScope.launch {
      doActionWithLoadingIndicator(
        givenContext = Dispatchers.IO,
        propertyTarget = _showLoadingIndicator
      ) {
        addressRepository.getKecamatan().let { resource ->
          when (resource) {
            is Resource.Success -> {
              _kecamatanList.postValue(resource.data!!)
              _showContentContainer.postValue(true)
            }
            is Resource.Failed -> {
              _showErrorContainer.postValue(true)
            }
          }
        }
      }
    }
  }

  private fun getKelurahan(kecamatanId: Long) {
    viewModelScope.launch {
      doActionWithLoadingIndicator(
        givenContext = Dispatchers.IO,
        propertyTarget = _showLoadingIndicator
      ) {
        addressRepository.getKelurahan(kecamatanId).let { resource ->
          when (resource) {
            is Resource.Success -> {
              _kelurahanList.postValue(resource.data!!)
              _showContentContainer.postValue(true)
            }
            is Resource.Failed -> {
              _showErrorContainer.postValue(true)
            }
          }
        }
      }
    }
  }

  fun onNavigationIconClick() {
    _whenBackButtonClicked.postValue(true)
  }

}