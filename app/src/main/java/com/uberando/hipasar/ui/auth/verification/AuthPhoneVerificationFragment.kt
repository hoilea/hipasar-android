package com.uberando.hipasar.ui.auth.verification

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentAuthPhoneVerificationBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthPhoneVerificationFragment : BaseFragment<FragmentAuthPhoneVerificationBinding>() {

  @Inject lateinit var authViewModel: AuthViewModel

  @Inject lateinit var viewModel: AuthPhoneVerificationViewModel

  private val arguments: AuthPhoneVerificationFragmentArgs by navArgs()

  override fun layoutResource(): Int = R.layout.fragment_auth_phone_verification

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    handleBackButtonClick { showExitConfirmation() }
  }

  override fun interactWithViewModel() {
    viewModel.setupPhoneNumber(arguments.phone)
    viewModel.codeIsValid.observe(viewLifecycleOwner) { valid ->
      if (valid) {
        authViewModel.tryToPostPhoneVerificationCode(viewModel.requireVerificationCode())
        findNavController().navigateUp()
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

}