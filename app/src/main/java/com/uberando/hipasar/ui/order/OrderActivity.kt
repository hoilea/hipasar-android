package com.uberando.hipasar.ui.order

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.ActivityOrderBinding
import com.uberando.hipasar.ui.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderActivity : BaseActivity<ActivityOrderBinding>() {

  private lateinit var navController: NavController

  override fun layoutResource(): Int = R.layout.activity_order

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val host: NavHostFragment = supportFragmentManager
      .findFragmentById(R.id.order_nav_host) as NavHostFragment ?: return
    navController = host.navController
  }

}