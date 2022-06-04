package com.uberando.hipasar.ui.main.account.setting.change.name

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentAccountChangeNameBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.components.TwsSnackbar
import com.uberando.hipasar.util.EventObserver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountChangeNameFragment : BaseFragment<FragmentAccountChangeNameBinding>() {

  @Inject
  lateinit var viewModel: AccountChangeNameViewModel

  private val arguments: AccountChangeNameFragmentArgs by navArgs()

  override fun layoutResource(): Int = R.layout.fragment_account_change_name

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
  }

  override fun interactWithViewModel() {
    viewModel.setupUser(arguments.user)
    arguments.user.name.let { name ->
      if (name != null && name != "null" && name != "") {
        viewModel.setupToolbarTitle(getString(R.string.str_change_name))
        viewModel.setupGuideMessage(getString(R.string.str_guide_change_name))
        viewModel.setupButtonText(getString(R.string.str_change))
      } else {
        viewModel.setupToolbarTitle(getString(R.string.str_set_name))
        viewModel.setupGuideMessage(getString(R.string.str_guide_set_name))
        viewModel.setupButtonText(getString(R.string.str_set))
      }
    }
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    })
    viewModel.whenUpdateNameSuccess.observe(viewLifecycleOwner, EventObserver { success ->
      if (success) {
        TwsSnackbar(
          binding!!.root,
          getString(R.string.str_account_change_name_success),
          Snackbar.LENGTH_SHORT
        ).show()
        findNavController().navigateUp()
      } else {
        TwsSnackbar(
          binding!!.root,
          getString(R.string.str_account_change_name_failed),
          Snackbar.LENGTH_SHORT
        ).show()
      }
    })
  }

}