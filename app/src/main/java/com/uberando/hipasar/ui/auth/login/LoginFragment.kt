package com.uberando.hipasar.ui.auth.login

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentLoginBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.auth.AuthViewModel
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.Converters
import com.uberando.hipasar.util.EventObserver
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uberando.hipasar.BuildConfig
import com.uberando.hipasar.ui.browser.BrowserActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

  @Inject
  lateinit var viewModel: LoginViewModel

  @Inject
  lateinit var authViewModel: AuthViewModel

  private lateinit var googleSignInClient: GoogleSignInClient

  private val activityResultLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    handleSignInWithGoogle(GoogleSignIn.getSignedInAccountFromIntent(result.data))
  }

  override fun layoutResource(): Int = R.layout.fragment_login

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
  }

  override fun interactWithViewModel() {
    setupGoogleSignInOption()
    authViewModel.whenRequireToShowResult.observe(viewLifecycleOwner, EventObserver { required ->
      if (required) {
        navigateToResult()
      }
    })
    viewModel.requireToSignIn.observe(viewLifecycleOwner, EventObserver { required ->
      if (required) {
        authViewModel.tryToPostSignInBasicData(
          viewModel.requirePhoneNumber(),
          viewModel.requirePassword()
        )
        navigateToResult()
      }
    })
    viewModel.whenRegisterButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToRegister()
      }
    })
    viewModel.whenGoogleButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        requireToSignInWithGoogle()
      }
    })
    viewModel.whenTermsButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToPrivacyPolicyBrowser()
      }
    })
    viewModel.whenForgotPasswordButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToPasswordRecovery()
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

  override fun onStart() {
    super.onStart()
    GoogleSignIn.getLastSignedInAccount(requireContext()).let { account ->
      if (account != null) {
        googleSignInClient.signOut()
      }
    }
  }

  /**
   * Google OAuth
   */
  private fun setupGoogleSignInOption() {
    val googleSignInOptions = GoogleSignInOptions.Builder(
      GoogleSignInOptions.DEFAULT_SIGN_IN
    ).requestEmail().build()
    googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)
  }

  private fun requireToSignInWithGoogle() {
    googleSignInClient.signInIntent
      .apply {
        putExtra(Constants.INTENT_EXTRA_REQUEST, Constants.GOOGLE_SIGN_IN_REQUEST_CODE)
        activityResultLauncher.launch(this)
      }
  }

  private fun handleSignInWithGoogle(completedTask: Task<GoogleSignInAccount>) {
    try {
      val account = completedTask.getResult(ApiException::class.java)
      authViewModel.tryToPostSignInOAuthData(
        Converters.convertGoogleSignInAccountToOAuthRequest(account)
      )
      navigateToResult()
    } catch (e: ApiException) {
      Timber.w(e)
    }
  }

  /**
   * Navigation trigger
   */
  private fun navigateToResult() {
    LoginFragmentDirections
      .actionLoginFragmentToAuthResultFragment()
      .apply {
        findNavController().navigate(this)
      }
  }

  private fun navigateToRegister() {
    LoginFragmentDirections
      .actionLoginFragmentToRegisterFragment()
      .apply {
        findNavController().navigate(this)
      }
  }

  private fun navigateToPasswordRecovery() {
    LoginFragmentDirections
      .actionLoginFragmentToPasswordRecoveryFragment()
      .apply {
        findNavController().navigate(this)
      }
  }

  private fun navigateToPrivacyPolicyBrowser() {
    Intent(requireActivity(), BrowserActivity::class.java).apply {
      this.putExtra(Constants.INTENT_EXTRA_PURPOSE, Constants.PURPOSE_PRIVACY_POLICY)
      this.putExtra(Constants.INTENT_EXTRA_URL, BuildConfig.PRIVACY_POLICY_URL)
      startActivity(this)
    }
  }

}