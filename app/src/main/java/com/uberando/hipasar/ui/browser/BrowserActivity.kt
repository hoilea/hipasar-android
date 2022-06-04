package com.uberando.hipasar.ui.browser

import android.content.Intent
import android.os.Bundle
import android.os.Message
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.ActivityBrowserBinding
import com.uberando.hipasar.ui.BaseActivity
import com.uberando.hipasar.util.Constants
import java.util.*

class BrowserActivity : BaseActivity<ActivityBrowserBinding>() {

  private var tabs = mutableListOf<BrowserFragment>()
  private var currentTab: BrowserFragment? = null
  private var mainTabId: String? = null

  override fun layoutResource(): Int = R.layout.activity_browser

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setupToolbar()
    initializeTab()
  }

  override fun onBackPressed() {
    val tempCurrentTab = currentTab
    if (tempCurrentTab != null) {
      val webCanGoBack = tempCurrentTab.webView.canGoBack()
      val tabMoreThanOne = tabs.size > 1
      if (webCanGoBack) {
        tempCurrentTab.webView.goBack()
      } else if (!webCanGoBack && tabMoreThanOne) {
        deleteTab(tempCurrentTab.browserId!!)
      } else {
        setResultCancel()
        finish()
      }
    } else {
      setResultCancel()
      super.onBackPressed()
    }
  }

  private fun setupToolbar() {
    binding?.browserFragmentToolbar?.setNavigationOnClickListener {
      setResultCancel()
      finish()
    }
  }

  private fun setResultCancel() {
    setResult(
      Constants.PAYMENT_REQUEST_CODE,
      Intent()
        .putExtra(
          Constants.INTENT_EXTRA_PAYMENT_CALLBACK,
          Constants.CALLBACK_CANCEL
        )
    )
  }

  private fun initializeTab() {
    val url = intent.getStringExtra(Constants.INTENT_EXTRA_URL)
    val purpose = intent.getStringExtra(Constants.INTENT_EXTRA_PURPOSE)
    val arguments = intent.getStringExtra(Constants.INTENT_EXTRA_ARGUMENTS)
    openNewTab(generateRandomUUIDToString(), url, true, purpose, arguments)
  }

  private fun openNewTab(
    tabId: String,
    url: String? = null,
    mainTab: Boolean = false,
    purpose: String? = Constants.PURPOSE_DEFAULT,
    arguments: String? = null
  ): BrowserFragment {
    val fragment = BrowserFragment.newInstance(tabId, url, purpose, arguments)
    tabs.add(fragment)
    val transaction = supportFragmentManager.beginTransaction()
    val tab = currentTab
    if (tab == null) {
      transaction.replace(R.id.browser_fragment_container, fragment, tabId)
    } else {
      transaction.hide(tab)
      transaction.add(R.id.browser_fragment_container, fragment, tabId)
    }
    if (mainTab) {
      mainTabId = tabId
    }
    transaction.commit()
    currentTab = fragment
    return fragment
  }

  private fun deleteTab(tabId: String) {
    val fragment = supportFragmentManager.findFragmentByTag(tabId) as BrowserFragment
    val transaction = supportFragmentManager.beginTransaction()
    transaction.detach(fragment)
    tabs.remove(fragment)
    currentTab = tabs.find { it.browserId == mainTabId }
    if (currentTab != null) {
      transaction.show(currentTab!!)
      transaction.commit()
    }
  }

  fun openMessageInNewTab(message: Message?) {
    val fragment = openNewTab(generateRandomUUIDToString(), null)
    fragment.messageFromPreviousTab = message
  }

  fun closeCurrentTab(tabId: String) {
    deleteTab(tabId)
  }

  private fun generateRandomUUIDToString() =
    UUID.randomUUID().toString()

}