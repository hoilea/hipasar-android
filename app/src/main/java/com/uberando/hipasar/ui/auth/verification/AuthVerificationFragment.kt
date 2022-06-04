package com.uberando.hipasar.ui.auth.verification

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentAuthVerificationBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthVerificationFragment : BaseFragment<FragmentAuthVerificationBinding>() {

  @Inject
  lateinit var authViewModel: AuthViewModel

  @Inject
  lateinit var viewModel: AuthVerificationViewModel

  private val arguments: AuthVerificationFragmentArgs by navArgs()

  override fun layoutResource(): Int = R.layout.fragment_auth_verification

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    handleBackButtonClick { showExitConfirmation() }
  }

  override fun interactWithViewModel() {
    arguments.hash?.let {
      authViewModel.tryToVerifyEmail(it)
      navigateToResult()
    }
    viewModel.whenCheckVerificationButtonClick.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        authViewModel.tryToCheckEmailVerificationStatus()
        navigateToResult()
      }
    }
  }

  private fun showExitConfirmation() {
    MaterialAlertDialogBuilder(requireContext())
      .setTitle(R.string.str_cancel_auth)
      .setMessage(R.string.str_msg_cancel_auth)
      .setPositiveButton(R.string.str_yes) { dialogInterface, _ ->
        dialogInterface.dismiss()
        requireActivity().finish()
      }
      .setNegativeButton(R.string.str_no) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .create()
      .show()
  }

  private fun navigateToResult() {
    AuthVerificationFragmentDirections
      .actionAuthVerificationFragmentToAuthResultFragment()
      .apply {
        findNavController().navigate(this)
      }
  }

}