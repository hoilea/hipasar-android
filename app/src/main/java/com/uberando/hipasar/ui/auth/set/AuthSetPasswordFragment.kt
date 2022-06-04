package com.uberando.hipasar.ui.auth.set

import androidx.navigation.fragment.findNavController
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentAuthSetPasswordBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.auth.AuthViewModel
import com.uberando.hipasar.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthSetPasswordFragment : BaseFragment<FragmentAuthSetPasswordBinding>() {

  @Inject
  lateinit var viewModel: AuthSetPasswordViewModel

  @Inject
  lateinit var authViewModel: AuthViewModel

  override fun layoutResource(): Int = R.layout.fragment_auth_set_password

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    handleBackButtonClick { /** Do Nothing */ }
  }

  override fun interactWithViewModel() {
    viewModel.whenSkipButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        authViewModel.requireToCancelSetPassword()
        findNavController().navigateUp()
      }
    })
    viewModel.whenRequireToSetPassword.observe(viewLifecycleOwner, EventObserver { required ->
      if (required) {
        authViewModel.tryToPostSetPasswordData(viewModel.requirePassword())
        findNavController().navigateUp()
      }
    })
  }
}