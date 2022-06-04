package com.uberando.hipasar.ui.address

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.ActivityAddressBinding
import com.uberando.hipasar.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressActivity : BaseActivity<ActivityAddressBinding>() {

  private lateinit var navController: NavController

  override fun layoutResource(): Int = R.layout.activity_address

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val host: NavHostFragment = supportFragmentManager
      .findFragmentById(R.id.order_nav_host) as NavHostFragment
    navController = host.navController
  }

}