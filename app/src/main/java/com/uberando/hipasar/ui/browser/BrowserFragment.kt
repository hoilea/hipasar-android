package com.uberando.hipasar.ui.browser

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentBrowserBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.util.Constants
import timber.log.Timber

class BrowserFragment : BaseFragment<FragmentBrowserBinding>() {
  
  var browserId: String? = null
  
  private var browserUrl: String? = null
  
  /**
   * browser purpose to determine right dependency for web view client
   */
  private var browserPurpose: String? = null
  
  /**
   * browser arguments is data require when performing some action
   * on browser, like order id and order price that required by
   * payment website to make payment.
   */
  private var browserArguments: String? = null

  lateinit var webView: WebView
  
  private lateinit var progressBar: ProgressBar
  
  private lateinit var webViewClient: WebViewClient
  
  private lateinit var webChromeClient: WebChromeClient

  var messageFromPreviousTab: Message? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      browserId = it.getString(BROWSER_ID)
      browserUrl = it.getString(BROWSER_URL)
      browserPurpose = it.getString(BROWSER_PURPOSE)
      browserArguments = it.getString(BROWSER_ARGUMENTS)
    }
  }

  override fun layoutResource(): Int = R.layout.fragment_browser

  override fun interactWithLayout() {
    binding?.let {
      webView = it.browserWebView
      progressBar = it.browserProgressBar
    }
    setupWebViewClient()
    setupWebChromeClient()
    setupWebView()
    browserUrl?.let {
      webView.loadUrl(it)
    }
  }

  override fun interactWithViewModel() = Unit

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (savedInstanceState == null) {
      messageFromPreviousTab?.let {
        processMessage(it)
      }
    }
  }

  private fun processMessage(message: Message) {
    val transport = message.obj as WebView.WebViewTransport
    transport.webView = webView
    message.sendToTarget()
  }

  private fun setupWebViewClient() {
    webViewClient = if (browserPurpose!! == Constants.PURPOSE_PAYMENT) {
      BrowserWebViewPaymentClient(browserArguments)
    } else {
      BrowserWebViewClient { requireActivity().finish() }
    }
  }

  private fun setupWebChromeClient() {
    webChromeClient = object : WebChromeClient() {
      override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
      ): Boolean {
        if (resultMsg?.obj is WebView.WebViewTransport) {
          (activity as BrowserActivity)
            .openMessageInNewTab(resultMsg)
          return true
        }
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
      }
      override fun onCloseWindow(window: WebView?) {
        super.onCloseWindow(window)
        (activity as BrowserActivity)
          .closeCurrentTab(browserId!!)
      }
      override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        progressBar.progress = newProgress
        if (newProgress < 100 && progressBar.visibility == View.GONE) {
          progressBar.visibility = View.VISIBLE
        } else if (newProgress == 100) {
          progressBar.visibility = View.GONE
        }
      }
    }
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun setupWebView() {
    webView.apply {
      webViewClient = this@BrowserFragment.webViewClient
      webChromeClient = this@BrowserFragment.webChromeClient
      settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        loadWithOverviewMode = true
        useWideViewPort = true
        builtInZoomControls = true
        javaScriptCanOpenWindowsAutomatically = true
        displayZoomControls = false
        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        setSupportMultipleWindows(true)
        setSupportZoom(true)
      }
      addJavascriptInterface(browserInterfaceObject(), "app")
    }
  }

  private fun browserInterfaceObject() =
    BrowserJSInterface(webView, browserArguments) { k, m -> handleAnyCallback(k, m) }

  private fun handleAnyCallback(key: String, message: Any?) {
    when (key) {
      Constants.KEY_TRANSACTION_FINISHED -> navigateToPaymentResult(Constants.CALLBACK_SUCCESS)
      Constants.KEY_TRANSACTION_FAILED -> navigateToPaymentResult(Constants.CALLBACK_FAILED)
      Constants.KEY_GET_ADDRESS -> navigateToOrigin(message.toString())
      else -> /** this block never called */ Timber.i("Unknown key appeared")
    }
  }

  private fun navigateToPaymentResult(callback: String) {
    val intent = Intent().apply { putExtra(Constants.INTENT_EXTRA_PAYMENT_CALLBACK, callback) }
    requireActivity().setResult(Constants.PAYMENT_REQUEST_CODE, intent)
    requireActivity().finish()
  }
  
  private fun navigateToOrigin(callback: String) {
    Intent().apply { putExtra(Constants.INTENT_EXTRA_ADDRESS_RESULT, callback) }.also { intent ->
      requireActivity().setResult(Constants.DAUM_REQUEST_CODE, intent)
      requireActivity().finish()
    }
  }

  companion object {

    private const val BROWSER_ID = "browser_id"
    private const val BROWSER_URL = "browser_url"
    private const val BROWSER_PURPOSE = "purpose"
    private const val BROWSER_ARGUMENTS = "arguments"

    fun newInstance(
      browserId: String,
      browserUrl: String? = null,
      purpose: String? = Constants.PURPOSE_DEFAULT,
      arguments: String? = null
    ) = BrowserFragment().apply {
      this.arguments = Bundle().apply {
        putString(BROWSER_ID, browserId)
        putString(BROWSER_URL, browserUrl)
        putString(BROWSER_PURPOSE, purpose)
        putString(BROWSER_ARGUMENTS, arguments)
      }
    }

  }

}