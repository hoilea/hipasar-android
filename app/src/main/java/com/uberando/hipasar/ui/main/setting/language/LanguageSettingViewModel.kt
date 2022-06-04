package com.uberando.hipasar.ui.main.setting.language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uberando.hipasar.ui.BaseViewModel
import javax.inject.Inject

class LanguageSettingViewModel @Inject constructor() : BaseViewModel() {
  
  private val _whenBackButtonClick = MutableLiveData<Boolean>()
  val whenBackButtonClick: LiveData<Boolean> get() = _whenBackButtonClick
  
  fun onBackButtonClick() {
    _whenBackButtonClick.postValue(true)
  }
  
}