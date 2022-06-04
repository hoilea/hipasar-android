package com.uberando.hipasar.ui.browser

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.uberando.hipasar.R
import com.uberando.hipasar.util.Constants
import timber.log.Timber

open class BrowserWebViewClient(private val onRequireExit: () -> Unit) : WebViewClient()  {

  override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
    val context = view.context
    return if (url.startsWith("intent://") || url.startsWith("shopeeid://")) {
      executeActionToOtherApplication(url, context)
    } else if (url == Constants.DURIAN_PAY_DANA_RETURN_URL) {
      onRequireExit()
      return false
    } else {
      view.loadUrl(url)
      return true
    }
  }

  override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
    val context = view.context
    val url = request.url.toString()
    return if (url.startsWith("intent://") || url.startsWith("shopeeid://")) {
      executeActionToOtherApplication(url, context)
    } else if (url == Constants.DURIAN_PAY_DANA_RETURN_URL) {
      onRequireExit()
      return false
    } else {
      view.loadUrl(request.url.toString())
      return true
    }
  }

  private fun executeActionToOtherApplication(url: String, context: Context): Boolean {
    try {
      val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
      try {
        context.startActivity(intent)
      } catch (e: ActivityNotFoundException) {
        try {
          val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${intent.`package`}"))
          val chooser = Intent.createChooser(viewIntent, context.getString(R.string.str_app_market_option))
          context.startActivity(chooser)
        } catch (e: Exception) {
          context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${intent.`package`}")))
        }
      }
    } catch (e: Exception) {
      Timber.e(e)
    }
    return true
  }

}