package com.uberando.hipasar.ui.main.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.uberando.hipasar.ui.BaseViewModel
import com.uberando.hipasar.util.Event
import javax.inject.Inject

class SettingViewModel @Inject constructor() : BaseViewModel() {
  
  private val _whenBackButtonClicked = MutableLiveData<Boolean>()
  val whenBackButtonClicked: LiveData<Boolean> get() = _whenBackButtonClicked
  
  private val _whenLanguageSettingClicked = MutableLiveData<Event<Boolean>>()
  val whenLanguageSettingClicked: LiveData<Event<Boolean>> get() = _whenLanguageSettingClicked
  
  fun onBackButtonClick() {
    _whenBackButtonClicked.postValue(true)
  }
  
  fun onLanguageSettingClick() {
    _whenLanguageSettingClicked.postValue(Event(true))
  }
  
}