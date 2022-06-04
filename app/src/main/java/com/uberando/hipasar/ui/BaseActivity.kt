package com.uberando.hipasar.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import timber.log.Timber
import java.util.*

abstract class BaseActivity<B : ViewDataBinding> : AppCompatActivity() {

  var binding: B? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    checkLocale()
    binding = DataBindingUtil.setContentView(this, layoutResource())
    binding?.lifecycleOwner = this
  }

  override fun onDestroy() {
    super.onDestroy()
    binding?.unbind()
    binding = null
  }

  @LayoutRes
  abstract fun layoutResource(): Int

  private fun checkLocale() {
    val sharedPref = getPreferences(Context.MODE_PRIVATE)
    sharedPref.getString(SHARED_PREF_KEY, "")?.let { result ->
      if (result != "") {
        Timber.i("result: $result")
        when (result) {
          Locale("id").toLanguageTag() -> setAppLocale(Locale("id"))
          Locale.ENGLISH.toLanguageTag() -> setAppLocale(Locale.ENGLISH)
          Locale.KOREAN.toLanguageTag() -> setAppLocale(Locale.KOREAN)
        }
      }
    }
  }

  private fun setAppLocale(locale: Locale) {
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
      createConfigurationContext(config)
    resources.updateConfiguration(config, resources.displayMetrics)
  }

  companion object {
    private const val SHARED_PREF_KEY = "language"
  }

}