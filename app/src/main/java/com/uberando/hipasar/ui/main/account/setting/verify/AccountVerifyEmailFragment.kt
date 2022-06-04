package com.uberando.hipasar.ui.main.account.setting.verify

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentAccountVerifyEmailBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.auth.verification.AuthVerificationViewModel
import com.uberando.hipasar.ui.components.TwsSnackbar
import com.uberando.hipasar.util.EventObserver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AccountVerifyEmailFragment : BaseFragment<FragmentAccountVerifyEmailBinding>() {

  @Inject
  lateinit var viewModel: AuthVerificationViewModel

  private val arguments: AccountVerifyEmailFragmentArgs by navArgs()

  override fun layoutResource(): Int = R.layout.fragment_account_verify_email

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    handleBackButtonClick()
  }

  override fun interactWithViewModel() {
    Timber.i("received hash: ${arguments.hash}")
    arguments.hash?.let { viewModel.requireToVerifyHashCode(it) }
    viewModel.verifyEmailSuccessful.observe(viewLifecycleOwner, EventObserver { success ->
      if (success) {
        navigateBack()
      } else {
        TwsSnackbar(
          binding!!.root,
          getString(R.string.str_account_change_email_failed),
          Snackbar.LENGTH_SHORT
        ).show()
      }
    })
  }

  private fun handleBackButtonClick() {
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        AlertDialog.Builder(requireContext())
          .setMessage(R.string.str_msg_change_email_cancel_question)
          .setPositiveButton(R.string.str_yes) { dialogInterface, _ ->
            dialogInterface.dismiss()
            navigateBack()
          }
          .setNegativeButton(R.string.str_no) { dialogInterface, _ ->
            dialogInterface.dismiss()
          }
          .create()
          .show()
      }
    })
  }

  private fun navigateToAccount() {
    AccountVerifyEmailFragmentDirections
      .actionAccountVerifyEmailFragmentToAccountFragment()
      .apply {
        findNavController().navigate(this)
      }
  }

  private fun navigateBack() {
    if (findNavController().previousBackStackEntry?.destination?.id == R.id.accountSettingFragment) {
      findNavController().navigateUp()
    } else {
      navigateToAccount()
    }
  }

}