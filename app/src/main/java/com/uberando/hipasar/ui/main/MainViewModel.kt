package com.uberando.hipasar.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {

  private val _showBottomNavigation = MutableLiveData<Boolean>()
  val showBottomNavigation: LiveData<Boolean> get() = _showBottomNavigation

  fun setBottomNavigationState(state: Boolean) {
    _showBottomNavigation.postValue(state)
  }

}