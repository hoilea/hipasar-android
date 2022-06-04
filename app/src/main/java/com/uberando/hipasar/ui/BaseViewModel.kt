package com.uberando.hipasar.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel() {
  
  suspend fun doActionWithLoadingIndicator(
    givenContext: CoroutineContext = Dispatchers.Default,
    propertyTarget: MutableLiveData<Boolean>,
    onBeforeAction: () -> Unit = {  },
    onAfterAction: () -> Unit = { },
    action: suspend () -> Unit
  ) {
    withContext(givenContext) {
      propertyTarget.postValue(true)
      onBeforeAction()
      action()
      onAfterAction()
      propertyTarget.postValue(false)
      
    }
  }
  
}