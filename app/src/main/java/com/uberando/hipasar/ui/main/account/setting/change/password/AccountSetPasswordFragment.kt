package com.uberando.hipasar.ui.main.account.setting.change.password

import androidx.navigation.fragment.findNavController
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentAccountSetPasswordBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.components.TwsSnackbar
import com.uberando.hipasar.util.EventObserver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountSetPasswordFragment : BaseFragment<FragmentAccountSetPasswordBinding>() {

  @Inject
  lateinit var viewModel: AccountChangePasswordViewModel

  override fun layoutResource(): Int = R.layout.fragment_account_set_password

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
  }

  override fun interactWithViewModel() {
    viewModel.modifyPasswordSuccessful.observe(viewLifecycleOwner, EventObserver { success ->
      if (success) {
        findNavController().navigateUp()
      } else {
        TwsSnackbar(
          binding!!.root,
          getString(R.string.str_account_set_password_failed),
          Snackbar.LENGTH_SHORT
        ).show()
      }
    })
  }
}