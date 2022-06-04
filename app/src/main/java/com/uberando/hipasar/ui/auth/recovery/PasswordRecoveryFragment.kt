package com.uberando.hipasar.ui.auth.recovery

import androidx.navigation.fragment.findNavController
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentPasswordRecoveryBinding
import com.uberando.hipasar.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PasswordRecoveryFragment : BaseFragment<FragmentPasswordRecoveryBinding>() {

  @Inject lateinit var viewModel: PasswordRecoveryViewModel

  override fun layoutResource() = R.layout.fragment_password_recovery

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
  }

  override fun interactWithViewModel() {
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    }
  }

}