package com.uberando.hipasar.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.ActivityMainBinding
import com.uberando.hipasar.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

  @Inject
  lateinit var viewModel: MainViewModel

  private lateinit var navController: NavController

  override fun layoutResource(): Int = R.layout.activity_main

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding?.viewModel = this.viewModel

    val host: NavHostFragment = supportFragmentManager
      .findFragmentById(R.id.main_nav_host) as NavHostFragment? ?: return
    navController = host.navController

    setNavControllerListener()
    setBottomNav()
  }

  override fun onSupportNavigateUp(): Boolean =
    navController.navigateUp() || super.onSupportNavigateUp()

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    Timber.i("intent: ${intent?.data}")
    handleDeeplinkIntent(intent)
  }

  private fun setNavControllerListener() {
    navController.addOnDestinationChangedListener { _, destination, _ ->
      when (destination.id) {
        R.id.homeFragment -> setBottomNavVisibility(true)
        R.id.userOrderOptionFragment -> setBottomNavVisibility(true)
        R.id.accountFragment -> setBottomNavVisibility(true)
        else -> setBottomNavVisibility(false)
      }
    }
  }

  private fun setBottomNav() {
    binding?.mainBottomNavigation?.setupWithNavController(navController)
    binding?.mainBottomNavigation?.setOnItemSelectedListener { menu ->
      val requireToNavigate = navController.previousBackStackEntry?.destination == null ||
        menu.itemId != navController.previousBackStackEntry?.destination?.id
      val notRequireToNavigate = menu.itemId == navController.previousBackStackEntry?.destination?.id

      if (requireToNavigate) {
        navController.navigate(menu.itemId)
      } else if (notRequireToNavigate) {
        navController.navigateUp()
      }

      true
    }
  }

  private fun setBottomNavVisibility(showBottomNav: Boolean) {
    viewModel.setBottomNavigationState(showBottomNav)
  }

  private fun handleDeeplinkIntent(intent: Intent?) {
    try {
      intent?.data?.let { uri ->
        if (uri.path != null && navController.currentDestination!!.id == R.id.accountVerifyEmailFragment) {
          navController.handleDeepLink(intent)
        }
      }
    } catch (e: Exception) {
      Timber.e(e)
    }
  }

}