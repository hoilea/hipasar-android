package com.uberando.hipasar.ui.main.account.setting.change.email

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentAccountChangeEmailBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.components.TwsSnackbar
import com.uberando.hipasar.util.EventObserver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountChangeEmailFragment : BaseFragment<FragmentAccountChangeEmailBinding>() {

  @Inject
  lateinit var viewModel: AccountChangeEmailViewModel

  private val arguments: AccountChangeEmailFragmentArgs by navArgs()

  override fun layoutResource(): Int = R.layout.fragment_account_change_email

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
  }

  override fun interactWithViewModel() {
    viewModel.setupUser(arguments.user)
    viewModel.setupToolbarTitle(getString(R.string.str_change_email))
    viewModel.setupGuideMessage(getString(R.string.str_guide_change_email))
    viewModel.setupButtonText(getString(R.string.str_change))
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    })
    viewModel.whenUpdateEmailSuccess.observe(viewLifecycleOwner, EventObserver { success ->
      if (success) {
        navigateToVerifyEmail()
      } else {
        TwsSnackbar(
          binding!!.root,
          getString(R.string.str_msg_update_email_failed),
          Snackbar.LENGTH_SHORT
        ).show()
      }
    })
  }

  private fun navigateToVerifyEmail() {
    AccountChangeEmailFragmentDirections
      .actionAccountChangeEmailFragmentToAccountVerifyEmailFragment(null)
      .apply {
        findNavController().navigate(this)
      }
  }

}