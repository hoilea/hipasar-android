package com.uberando.hipasar.ui.main.setting

import androidx.navigation.fragment.findNavController
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentSettingBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>() {
  
  @Inject lateinit var viewModel: SettingViewModel
  
  override fun layoutResource() = R.layout.fragment_setting
  
  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
  }
  
  override fun interactWithViewModel() {
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    }
    viewModel.whenLanguageSettingClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToLanguageSetting()
      }
    })
  }
  
  private fun navigateToLanguageSetting() {
    SettingFragmentDirections
      .actionSettingFragmentToLanguageSettingFragment()
      .apply { findNavController().navigate(this) }
  }
 
}