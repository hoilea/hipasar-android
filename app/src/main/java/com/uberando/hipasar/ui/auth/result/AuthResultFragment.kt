package com.uberando.hipasar.ui.auth.result

import android.content.Intent
import androidx.navigation.fragment.findNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentAuthResultBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.auth.AuthViewModel
import com.uberando.hipasar.ui.main.MainActivity
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AuthResultFragment : BaseFragment<FragmentAuthResultBinding>() {

  @Inject
  lateinit var viewModel: AuthViewModel

  override fun layoutResource(): Int = R.layout.fragment_auth_result

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    handleBackButtonClick { /** Do Nothing */ }
  }

  override fun interactWithViewModel() {
    viewModel.signUpSuccessful.observe(viewLifecycleOwner, EventObserver { successful ->
      if (successful) {
        navigateToComparePhone(viewModel.requireCachedPhoneNumber())
      }
    })
    viewModel.signInOAuthSuccessful.observe(viewLifecycleOwner, EventObserver { successful ->
      if (successful) {
        if (viewModel.oAuthPasswordAvailable()) {
          navigateToMain(Constants.SUCCESS)
        } else {
          navigateToSetPassword()
        }
      }
    })
    viewModel.signInBasicSuccessful.observe(viewLifecycleOwner, EventObserver { successful ->
      if (successful) {
        navigateToMain(Constants.SUCCESS)
      }
    })
    viewModel.setPasswordSuccessful.observe(viewLifecycleOwner, EventObserver { successful ->
      if (successful) {
        navigateToMain(Constants.SUCCESS)
      }
    })
    viewModel.verifyEmailSuccessful.observe(viewLifecycleOwner, EventObserver { successful ->
      if (successful) {
        navigateToMain(Constants.SUCCESS)
      }
    })
    viewModel.whenSetPasswordCancelled.observe(viewLifecycleOwner, EventObserver { cancelled ->
      if (cancelled) {
        navigateToMain(Constants.SUCCESS)
      }
    })
    viewModel.whenRetryButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        when {
          viewModel.visitingContext() == Constants.AUTH_VISITING_CONTEXT_SIGN_IN -> {
            navigateToLogin()
          }
          viewModel.visitingContext() == Constants.AUTH_VISITING_CONTEXT_REGISTER -> {
            navigateToRegister()
          }
          viewModel.visitingContext() == Constants.AUTH_VISITING_CONTEXT_VERIFY_EMAIL -> {
            viewModel.requireToRetryEmailVerification()
          }
          else -> {
            navigateToMain(Constants.FAILED)
          }
        }
      }
    })
    viewModel.whenCancelButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToMain(Constants.FAILED)
      }
    })
    viewModel.whenSetPasswordRetryButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToSetPassword()
      }
    })
    viewModel.whenUserPhoneNotVerified.observe(viewLifecycleOwner, EventObserver { notVerified ->
      if (notVerified) {
        navigateToComparePhone(viewModel.requireCachedPhoneNumber())
      }
    })
    viewModel.compareVerificationCodeSuccessful.observe(viewLifecycleOwner, EventObserver { successful ->
      if (successful) {
        navigateToMain(Constants.SUCCESS)
      } else {
        navigateToMain(Constants.FAILED)
      }
    })
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.requireToClearAuthResult()
  }

  private fun retrieveFirebaseToken() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
      if (!task.isSuccessful) {
        Timber.w("Fetch FCM registration token failed.")
        return@addOnCompleteListener
      }
      val token = task.result
      viewModel.requireToSaveFirebaseToken(token)
    }
  }

  private fun navigateToMain(resultCode: Int) {
    if (resultCode == Constants.SUCCESS) {
      retrieveFirebaseToken()
    }
    Intent(requireActivity(), MainActivity::class.java)
      .apply {
        putExtra(Constants.INTENT_EXTRA_RESULT_STATE, resultCode)
        requireActivity().setResult(Constants.AUTHENTICATION_REQUEST_CODE, this)
        requireActivity().finish()
        startActivity(this)
      }
  }

  private fun navigateToVerifyEmail() {
    AuthResultFragmentDirections
      .actionAuthResultFragmentToAuthVerificationFragment(null)
      .apply {
        findNavController().navigate(this)
      }
  }

  private fun navigateToComparePhone(phone: String) {
    AuthResultFragmentDirections
      .actionAuthResultFragmentToAuthPhoneVerificationFragment(phone)
      .apply {
        findNavController().navigate(this)
      }
  }

  private fun navigateToSetPassword() {
    AuthResultFragmentDirections
      .actionAuthResultFragmentToAuthSetPasswordFragment()
      .apply {
        findNavController().navigate(this)
      }
  }

  private fun navigateToLogin() {
    AuthResultFragmentDirections
      .actionAuthResultFragmentToLoginFragment()
      .apply {
        findNavController().navigate(this)
      }
  }

  private fun navigateToRegister() {
    AuthResultFragmentDirections
      .actionAuthResultFragmentToRegisterFragment()
      .apply {
        findNavController().navigate(this)
      }
  }

}