package com.uberando.hipasar.ui.main.setting.language

import android.content.Context
import android.os.Build
import androidx.navigation.fragment.findNavController
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentLanguageSettingBinding
import com.uberando.hipasar.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LanguageSettingFragment : BaseFragment<FragmentLanguageSettingBinding>() {
  
  @Inject lateinit var viewModel: LanguageSettingViewModel
  
  override fun layoutResource() = R.layout.fragment_language_setting
  
  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    setupRadioGroup()
  }
  
  override fun interactWithViewModel() {
    viewModel.whenBackButtonClick.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    }
  }
  
  private fun setupRadioGroup() {
    getAppLocale { localeId ->
      binding?.languageSettingGroup?.check(localeId)
    }
    binding?.languageSettingGroup?.setOnCheckedChangeListener { _, checkedId ->
      setAppLocale(checkedId.toAndroidLocale())
    }
  }
  
  private fun setAppLocale(language: String = Locale.getDefault().language) {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
      requireActivity().createConfigurationContext(config)
    resources.updateConfiguration(config, resources.displayMetrics)
    savePreference(locale)
    navigateUp()
  }

  private fun savePreference(locale: Locale) {
    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()) {
      putString(SHARED_PREF_KEY, locale.toLanguageTag())
      apply()
    }
  }
  
  private fun getAppLocale(currentLocale: (localeId: Int) -> Unit) {
    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
    sharedPref?.getString(SHARED_PREF_KEY, "")?.let { result ->
      Timber.i("shared pref result: $result")
      if (result != "") {
        when (result) {
          Locale("id").toLanguageTag() -> currentLocale(R.id.language_indonesia)
          Locale.ENGLISH.toLanguageTag() -> currentLocale(R.id.language_english)
//          Locale.KOREAN.toLanguageTag() -> currentLocale(R.id.language_korean)
        }
      } else {
        Locale.getDefault().let { locale ->
          when (locale.language) {
            Locale("id").language -> currentLocale(R.id.language_indonesia)
            Locale.ENGLISH.language -> currentLocale(R.id.language_english)
//            Locale.KOREAN.language -> currentLocale(R.id.language_korean)
          }
        }
      }
    }
  }
  
  private fun Int.toAndroidLocale(): String {
    return when (this) {
      R.id.language_indonesia -> Locale("id").language
      R.id.language_english -> Locale.ENGLISH.language
//      R.id.language_korean -> Locale.KOREAN.language
      else -> Locale.getDefault().language
    }
  }
  
  private fun navigateUp() {
    findNavController().navigateUp()
  }

  companion object {
    private const val SHARED_PREF_KEY = "language"
  }
  
}