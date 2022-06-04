package com.uberando.hipasar.ui.main.order.detail

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.uberando.hipasar.BuildConfig
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentUserOrderDetailBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.adapter.order.product.OrderProductAdapter
import com.uberando.hipasar.ui.auth.AuthActivity
import com.uberando.hipasar.ui.browser.BrowserActivity
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserOrderDetailFragment : BaseFragment<FragmentUserOrderDetailBinding>() {
  
  @Inject
  lateinit var viewModel: UserOrderDetailViewModel
  
  private val arguments: UserOrderDetailFragmentArgs by navArgs()
  
  private lateinit var recyclerViewAdapter: OrderProductAdapter
  
  private val activityResultLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    handleResult(result)
  }
  
  private fun handleResult(result: ActivityResult) {
    when (result.resultCode) {
      Constants.PAYMENT_REQUEST_CODE -> {
        result.data?.let { intent ->
          handlePaymentCallback(
            intent.getStringExtra(Constants.INTENT_EXTRA_PAYMENT_CALLBACK)!!
          )
        }
      }
      Constants.AUTHENTICATION_REQUEST_CODE -> {
        result.data?.let { intent ->
          when (intent.getIntExtra(Constants.INTENT_EXTRA_RESULT_STATE, 0)) {
            Constants.SUCCESS -> {
              viewModel.requireToGetOrder()
              somethingChangedOnThisFragment()
            }
            else -> Unit
          }
        }
      }
    }
  }
  
  override fun layoutResource(): Int = R.layout.fragment_user_order_detail
  
  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    setupRecyclerViewAdapter()
    setupRecyclerView()
  }
  
  override fun interactWithViewModel() {
    arguments.orderId.let { if (it != "-1") viewModel.requireToInitializeOrder(it) }
    viewModel.order.observe(viewLifecycleOwner) { order ->
      if (order != null) {
        recyclerViewAdapter.submitList(order.products)
      }
    }
    viewModel.requireToPayOrder.observe(viewLifecycleOwner, EventObserver { required ->
      if (required) {
        navigateToBrowser()
      }
    })
    viewModel.whenUpdateOrderFailed.observe(viewLifecycleOwner, EventObserver { failed ->
      if (failed) {
        showOrderResultMessage(
          title = "",
          message = getString(R.string.str_msg_update_order_failed)
        )
      } else if (!failed) {
        somethingChangedOnThisFragment()
        showOrderResultMessage(
          title = "",
          message = getString(R.string.str_msg_update_order_success)
        )
      }
    })
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    })
    viewModel.whenLoginButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToAuth()
      }
    })
    viewModel.whenCancelOrderSuccess.observe(viewLifecycleOwner, EventObserver { success ->
      if (success) {
        viewModel.requireToQuietlyUpdateOrderStatus(cancel = true)
        somethingChangedOnThisFragment()
        showOrderResultMessage(
          "", getString(R.string.str_msg_order_cancel_success)
        )
      } else {
        showOrderResultMessage(
          "", getString(R.string.str_msg_order_cancel_failed)
        )
      }
    })
    viewModel.showLoadingDialog.observe(viewLifecycleOwner) { show ->
      if (show) {
        loadingDialog.show()
      } else {
        loadingDialog.dismiss()
      }
    }
  }
  
  private fun setupRecyclerViewAdapter() {
    recyclerViewAdapter = OrderProductAdapter()
  }
  
  private fun setupRecyclerView() {
    binding?.orderProductRecyclerView?.adapter = recyclerViewAdapter
  }
  
  private fun handlePaymentCallback(callback: String) {
    when (callback) {
      Constants.CALLBACK_SUCCESS -> {
        viewModel.requireToQuietlyUpdateOrderStatus()
        somethingChangedOnThisFragment()
        showOrderResultMessage(
          getString(R.string.str_payment_title_succeed),
          getString(R.string.str_payment_description_succeed)
        )
      }
      Constants.CALLBACK_CANCEL -> {
        showOrderResultMessage(
          getString(R.string.str_payment_title_canceled),
          getString(R.string.str_payment_description_canceled)
        )
      }
      Constants.CALLBACK_FAILED -> {
        showOrderResultMessage(
          getString(R.string.str_payment_title_failed),
          getString(R.string.str_payment_description_failed)
        )
      }
    }
  }
  
  private fun showOrderResultMessage(title: String, message: String) {
    AlertDialog.Builder(requireContext())
      .setTitle(title)
      .setMessage(message)
      .setPositiveButton(R.string.str_ok) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .create()
      .show()
  }
  
  private fun somethingChangedOnThisFragment() {
    setupSavedState(Constants.KEY_ORDER_MODIFIED, true)
  }
  
  private fun navigateToAuth() {
    Intent(requireContext(), AuthActivity::class.java).apply {
      activityResultLauncher.launch(this)
    }
  }
  
  private fun navigateToBrowser() {
    Intent(requireActivity(), BrowserActivity::class.java).apply {
      this.putExtra(Constants.INTENT_EXTRA_URL, BuildConfig.PAYMENT_URL)
      this.putExtra(Constants.INTENT_EXTRA_PURPOSE, Constants.PURPOSE_PAYMENT)
      this.putExtra(Constants.INTENT_EXTRA_ARGUMENTS, viewModel.requirePaymentArguments())
      activityResultLauncher.launch(this)
    }
  }
  
}