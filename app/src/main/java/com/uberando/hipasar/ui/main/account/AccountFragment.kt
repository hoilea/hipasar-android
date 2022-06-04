package com.uberando.hipasar.ui.main.account

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.uberando.hipasar.BuildConfig
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentAccountBinding
import com.uberando.hipasar.entity.User
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.auth.AuthActivity
import com.uberando.hipasar.ui.browser.BrowserActivity
import com.uberando.hipasar.ui.main.MainActivity
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>() {

  @Inject
  lateinit var viewModel: AccountViewModel

  private var navigationIndicator = 0

  override fun layoutResource(): Int = R.layout.fragment_account

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    setHasOptionsMenu(true)
    setupToolbarMenu()
  }

  override fun interactWithViewModel() {
    viewModel.whenLoginButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToAuth()
      }
    })
    viewModel.whenLogoutClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        AlertDialog.Builder(requireContext())
          .setMessage(R.string.str_logout_question)
          .setPositiveButton(R.string.str_yes) { dialogInterface, _ ->
            dialogInterface.dismiss()
            viewModel.requireToSignOut()
          }
          .setNegativeButton(R.string.str_no) { dialogInterface, _ ->
            dialogInterface.dismiss()
          }
          .create()
          .show()
      }
    })
    viewModel.signOutSuccess.observe(viewLifecycleOwner, EventObserver { success ->
//      if (success) {
//        navigateToMain()
//      }
    })
    viewModel.whenAccountClicked.observe(viewLifecycleOwner, EventObserver { success ->
      if (success) {
        navigateToAccountSetting(viewModel.requireUserData()!!)
      }
    })
    viewModel.whenAddressClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToAddress()
      }
    })
    viewModel.whenCartClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToCart()
      }
    })
    viewModel.whenChatClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        Toast.makeText(requireContext(), getString(R.string.str_coming_soon), Toast.LENGTH_SHORT).show()
      }
    })
    viewModel.whenPrivacyPolicyContainerClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToPrivacyPolicyBrowser()
      }
    })
    viewModel.whenContactUsContainerClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToWhatsAppMessage()
      }
    })
  }

  override fun onResume() {
    super.onResume()
    if (navigationIndicator > 0) {
      viewModel.quietlyGetProfile()
      navigationIndicator -= 1
    }
  }
  
  private fun setupToolbarMenu() {
    binding?.menuToolbar?.setOnMenuItemClickListener { menu ->
      when (menu.itemId) {
        R.id.item_settings -> {
          navigateToSettings()
          true
        }
        else -> false
      }
    }
  }

  private fun navigateToAuth() {
    navigationIndicator += 1
    Intent(requireActivity(), AuthActivity::class.java).apply {
      startActivity(this)
    }
  }

  private fun navigateToMain() {
    Intent(requireActivity(), MainActivity::class.java).apply {
      startActivity(this)
    }
    requireActivity().finish()
  }
  
  private fun navigateToSettings() {
    AccountFragmentDirections
      .actionAccountFragmentToSettingFragment()
      .apply { findNavController().navigate(this) }
  }

  private fun navigateToAccountSetting(user: User) {
    AccountFragmentDirections
      .actionAccountFragmentToAccountSettingFragment(user)
      .apply {
        findNavController().navigate(this)
      }
  }

  private fun navigateToAddress() {
    AccountFragmentDirections
      .actionAccountFragmentToAddressFragment()
      .apply {
        findNavController().navigate(this)
      }
  }
  
  private fun navigateToCart() {
    AccountFragmentDirections
      .actionAccountFragmentToCartFragment()
      .apply { findNavController().navigate(this) }
  }

  private fun navigateToPrivacyPolicyBrowser() {
    Intent(requireActivity(), BrowserActivity::class.java).apply {
      this.putExtra(Constants.INTENT_EXTRA_PURPOSE, Constants.PURPOSE_PRIVACY_POLICY)
      this.putExtra(Constants.INTENT_EXTRA_URL, BuildConfig.PRIVACY_POLICY_URL)
      startActivity(this)
    }
  }

  private fun navigateToWhatsAppMessage() {
    val url = "https://api.whatsapp.com/send?phone=+6282138239674"
    Intent(Intent.ACTION_VIEW).apply {
      data = Uri.parse(url)
      startActivity(this)
    }
  }

}