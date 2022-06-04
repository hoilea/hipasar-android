package com.uberando.hipasar.ui.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.uberando.hipasar.ui.BaseViewModel
import com.uberando.hipasar.util.Event
import javax.inject.Inject

class SearchQueryViewModel @Inject constructor() : BaseViewModel() {

  val query = MutableLiveData<String>()

  val showClearButton = Transformations.map(query) {
    it != null && it != ""
  }

  private val _whenBackButtonClicked = MutableLiveData<Boolean>()
  val whenBackButtonClicked: LiveData<Boolean> get() = _whenBackButtonClicked

  private val _whenImeActionClicked = MutableLiveData<Boolean>()
  val whenImeActionClicked: LiveData<Boolean> get() = _whenImeActionClicked

  private val _showQueryEmptyMessage = MutableLiveData<Event<Boolean>>()
  val showQueryEmptyMessage: LiveData<Event<Boolean>> get() = _showQueryEmptyMessage

  fun onBackButtonClick() {
    _whenBackButtonClicked.postValue(true)
  }

  fun onClearButtonClick() {
    query.value = ""
  }

  fun onImeActionClick() {
    if (query.value != null && query.value != "") {
      _whenImeActionClicked.postValue(true)
    } else {
      _showQueryEmptyMessage.postValue(Event(true))
    }
  }

  fun requireSearchQuery() = query.value.toString()

}