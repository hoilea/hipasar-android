package com.uberando.hipasar.ui.browser

import android.webkit.WebView
import com.uberando.hipasar.util.findOrderCode
import com.uberando.hipasar.util.findOrderPrice
import timber.log.Timber

class BrowserWebViewPaymentClient(
  private val arguments: String?,
  private val onRequireExit: () -> Unit = {}
) : BrowserWebViewClient(onRequireExit) {
  
  private var firstLaunch = true
  
  override fun onPageFinished(view: WebView?, url: String?) {
    super.onPageFinished(view, url)
    if (firstLaunch) {
      executeRequiredPaymentAction(view)
    }
  }
  
  private fun executeRequiredPaymentAction(view: WebView?) {
    view?.evaluateJavascript("on_pay(${arguments.findOrderPrice()}, ${arguments.findOrderCode()})") { p0 ->
      Timber.i("onReceive value $p0")
    }
  }
  
}