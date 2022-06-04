package com.uberando.hipasar.ui.auth.register

import android.content.Intent
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uberando.hipasar.BuildConfig
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentRegisterBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.auth.AuthViewModel
import com.uberando.hipasar.ui.browser.BrowserActivity
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {

  @Inject
  lateinit var viewModel: RegisterViewModel

  @Inject
  lateinit var authViewModel: AuthViewModel

  override fun layoutResource(): Int = R.layout.fragment_register

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
  }

  override fun interactWithViewModel() {
    authViewModel.whenRequireToShowResult.observe(viewLifecycleOwner, EventObserver { required ->
      if (required) {
        navigateToResult()
      }
    })
    viewModel.requireToRegister.observe(viewLifecycleOwner, EventObserver { required ->
      if (required) {
        authViewModel.tryToPostSignUpData(viewModel.requireSignUpData())
        navigateToResult()
      }
    })
    viewModel.whenLoginButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToLogin()
      }
    })
    viewModel.whenTermsButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToPrivacyPolicyBrowser()
      }
    })
    viewModel.showInvalidInputMessage.observe(viewLifecycleOwner, EventObserver { show ->
      if (show) {
        MaterialAlertDialogBuilder(requireContext())
          .setTitle(R.string.str_invalid_input)
          .setMessage(R.string.str_msg_invalid_input)
          .setPositiveButton(R.string.str_ok) { dialogInterface, _ ->
            dialogInterface.dismiss()
          }
          .create()
          .show()
      }
    })
    viewModel.showTermsNotCheckedMessage.observe(viewLifecycleOwner, EventObserver { show ->
      if (show) {
        MaterialAlertDialogBuilder(requireContext())
          .setTitle(R.string.str_agreement_required)
          .setMessage(R.string.str_msg_agreement_required)
          .setPositiveButton(R.string.str_ok) { dialogInterface, _ ->
            dialogInterface.dismiss()
          }
          .create()
          .show()
      }
    })
  }

  private fun navigateToResult() {
    RegisterFragmentDirections
      .actionRegisterFragmentToAuthResultFragment()
      .apply {
        findNavController().navigate(this)
      }
  }

  private fun navigateToLogin() {
    RegisterFragmentDirections
      .actionRegisterFragmentToLoginFragment()
      .apply { findNavController().navigate(this) }
  }

  private fun navigateToPrivacyPolicyBrowser() {
    Intent(requireActivity(), BrowserActivity::class.java).apply {
      this.putExtra(Constants.INTENT_EXTRA_PURPOSE, Constants.PURPOSE_PRIVACY_POLICY)
      this.putExtra(Constants.INTENT_EXTRA_URL, BuildConfig.PRIVACY_POLICY_URL)
      startActivity(this)
    }
  }

}