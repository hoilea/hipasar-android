package com.uberando.hipasar.ui.browser

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.findOrderId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber

class BrowserJSInterface(
  private val webView: WebView,
  private val arguments: String?,
  private val websiteCallback: (key: String, message: Any?) -> Unit
) {

  @JavascriptInterface
  fun responseAddress(parameters: String) {
    websiteCallback(Constants.KEY_GET_ADDRESS, parameters)
  }

  @JavascriptInterface
  fun updateOrder() {
    runBlocking {
      withContext(Dispatchers.Main) {
        webView.evaluateJavascript("updateOrder(${arguments?.findOrderId()})") {
          Timber.i("order updated $it")
        }
      }
    }
  }

  @JavascriptInterface
  fun finishTransaction() {
    websiteCallback(Constants.KEY_TRANSACTION_FINISHED, null)
  }

  @JavascriptInterface
  fun failedTransaction() {
    websiteCallback(Constants.KEY_TRANSACTION_FAILED, null)
  }

  @JavascriptInterface
  fun requestOrderId() {
    runBlocking {
      withContext(Dispatchers.Main) {
        webView.evaluateJavascript("setOrderId(${arguments?.findOrderId()})") {
          Timber.i("order id set")
        }
      }
    }
  }

}