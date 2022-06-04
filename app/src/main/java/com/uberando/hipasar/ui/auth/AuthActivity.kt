package com.uberando.hipasar.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.ActivityAuthBinding
import com.uberando.hipasar.ui.BaseActivity
import com.uberando.hipasar.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : BaseActivity<ActivityAuthBinding>() {

  @Inject
  lateinit var viewModel: AuthViewModel

  private lateinit var navController: NavController

  override fun layoutResource(): Int = R.layout.activity_auth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val host: NavHostFragment = supportFragmentManager
      .findFragmentById(R.id.auth_nav_host) as NavHostFragment? ?: return
    navController = host.navController
  }

  override fun onBackPressed() {
    setCancelResult()
    super.onBackPressed()
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    Timber.i("onNewIntent: ${intent?.data}")
    intent?.data?.let {
      Timber.i("try to verify: $it")
//      viewModel.tryToVerifyEmail(it.toString())
      navController.handleDeepLink(intent)
    }
  }

  private fun setCancelResult() {
    setResult(Constants.AUTHENTICATION_REQUEST_CODE,
    intent.putExtra(
      Constants.INTENT_EXTRA_RESULT_STATE, Constants.CANCELED
    ))
  }

}