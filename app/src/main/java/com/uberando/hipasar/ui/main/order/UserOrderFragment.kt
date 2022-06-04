package com.uberando.hipasar.ui.main.order

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.uberando.hipasar.BuildConfig
import com.uberando.hipasar.R
import com.uberando.hipasar.entity.Order
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.adapter.order.OrderAdapter
import com.uberando.hipasar.ui.adapter.order.OrderListener
import com.uberando.hipasar.ui.auth.AuthActivity
import com.uberando.hipasar.ui.browser.BrowserActivity
import com.uberando.hipasar.ui.components.TwsSnackbar
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.EventObserver
import com.uberando.hipasar.util.buildPaymentArguments
import com.google.android.material.snackbar.Snackbar
import com.uberando.hipasar.databinding.FragmentUserOrderBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserOrderFragment : BaseFragment<FragmentUserOrderBinding>() {

  @Inject
  lateinit var viewModel: UserOrderViewModel

  private lateinit var orderAdapter: OrderAdapter

  private val arguments: UserOrderFragmentArgs by navArgs()

  private val activityResultLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    handleActivityResult(result)
  }

  override fun layoutResource(): Int = R.layout.fragment_user_order

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    setupRecyclerViewAdapter()
    setupRecyclerView()
  }

  override fun interactWithViewModel() {
    viewModel.setupOrderFilter(arguments.filter)
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    }
    viewModel.whenLoginButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToAuth()
      }
    })
    viewModel.orders.observe(viewLifecycleOwner) { orders ->
      orderAdapter.submitList(orders)
    }
    viewModel.filters.observe(viewLifecycleOwner) { filters ->
//      setupFilters(filters)
    }
    viewModel.getFilterFailed.observe(viewLifecycleOwner, EventObserver { failed ->
      if (failed) {
        TwsSnackbar(
          binding!!.root,
          getString(R.string.str_msg_get_filter_failed),
          Snackbar.LENGTH_SHORT
        ).show()
      }
    })
    viewModel.cancelOrderFailed.observe(viewLifecycleOwner, EventObserver { failed ->
      if (failed) {
        TwsSnackbar(
          binding!!.root,
          getString(R.string.str_msg_cancel_order_failed),
          Snackbar.LENGTH_SHORT
        ).show()
      }
    })
    viewModel.confirmOrderFailed.observe(viewLifecycleOwner, EventObserver { failed ->
      if (failed) {
        TwsSnackbar(
          binding!!.root,
          getString(R.string.str_msg_confirm_order_failed),
          Snackbar.LENGTH_SHORT
        ).show()
      }
    })
    receiveSavedState<Boolean>(Constants.KEY_ORDER_MODIFIED) { modified ->
      if (modified) {
        viewModel.requireToRefreshOrder()
      }
    }
  }

  private fun handleActivityResult(result: ActivityResult) {
    when (result.resultCode) {
      Constants.AUTHENTICATION_REQUEST_CODE -> {
        result.data?.let { intent ->
          when (intent.getIntExtra(Constants.INTENT_EXTRA_RESULT_STATE, Constants.FAILED)) {
            Constants.SUCCESS -> {
              viewModel.requireToRefreshOrder()
            }
            Constants.FAILED -> {
              showAuthFailedAlert()
            }
            Constants.CANCELED -> {
              showAuthCancelledAlert()
            }
          }
        }
      }
      Constants.PAYMENT_REQUEST_CODE -> {
        result.data?.let { intent ->
          when (intent.getStringExtra(Constants.INTENT_EXTRA_PAYMENT_CALLBACK)!!) {
            Constants.CALLBACK_SUCCESS -> {
              viewModel.requireToRefreshOrder()
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
      }
    }
  }

  private fun setupRecyclerViewAdapter() {
    orderAdapter = OrderAdapter(OrderListener(
      itemClicked = { navigateToOrderDetail(it.id) },
      orderPay = { navigateToPayment(it) },
      orderCancelled = { handleCancelOrder(it) },
      orderArrived = { handleArriveOrder(it) },
      orderFinished = { handleFinishOrder(it) }
    ))
  }

  private fun setupRecyclerView() {
    binding?.transactionRecyclerView?.adapter = orderAdapter
  }
  
//  private fun setupFilters(filters: List<Filter>) {
//    val chipGroup = binding?.orderChipGroup
//    filters.forEach { filter ->
//      if (filter.name.isNotEmpty() && filter.name.isNotBlank()) {
//        chipGroup?.addView(
//          Chip(requireContext()).apply {
//            this.id = filter.code
//            this.text = filter.code.buildFilterName()
//            this.isCheckable = true
//            this.setOnCheckedChangeListener { _, _ ->
//              handleChipCheck()
//            }
//          }
//        )
//      }
//    }
//  }
  
  private var removeChipAvailable = false
  
//  private fun handleChipCheck() {
//    val chipGroup = binding?.orderChipGroup!!
//    viewModel.requireToFilterOrders(chipGroup.checkedChipIds)
//    if (chipGroup.checkedChipIds.isNotEmpty() && !removeChipAvailable) {
//      chipGroup.addView(
//        Chip(requireContext()).apply {
//          this.isCheckable = false
//          this.text = getString(R.string.str_reset)
//          this.setChipBackgroundColorResource(R.color.red_900_light)
//          this.setChipStrokeColorResource(R.color.white)
//          this.setTextColor(resources.getColor(R.color.white))
//          this.setOnClickListener {
//            chipGroup.clearCheck()
//            chipGroup.removeView(this)
//          }
//        }, 0
//      )
//      removeChipAvailable = true
//    } else if (chipGroup.checkedChipIds.isEmpty()) {
//      chipGroup.removeViewAt(0)
//      removeChipAvailable  = false
//    }
//  }
  
  private fun handleCancelOrder(order: Order) {
    AlertDialog.Builder(requireContext())
      .setMessage(R.string.str_question_cancel_order)
      .setPositiveButton(R.string.str_yes) { dialogInterface, _ ->
        dialogInterface.dismiss()
        viewModel.requireToCancelOrder(order)
      }
      .setNegativeButton(R.string.str_no) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .create()
      .show()
  }
  
  private fun handleArriveOrder(order: Order) {
    AlertDialog.Builder(requireContext())
      .setMessage(R.string.str_question_confirm_order_arrive)
      .setPositiveButton(R.string.str_yes) { dialogInterface, _ ->
        dialogInterface.dismiss()
        viewModel.requireToConfirmArrivedOrder(order)
      }
      .setNegativeButton(R.string.str_no) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .create()
      .show()
  }
  
  private fun handleFinishOrder(order: Order) {
    AlertDialog.Builder(requireContext())
      .setMessage(R.string.str_question_finish_order)
      .setPositiveButton(R.string.str_yes) { dialogInterface, _ ->
        dialogInterface.dismiss()
        viewModel.requireToConfirmArrivedOrder(order)
      }
      .setNegativeButton(R.string.str_no) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .create()
      .show()
  }

  private fun showAuthFailedAlert() {
    AlertDialog.Builder(requireContext())
      .setMessage(R.string.str_msg_auth_failed)
      .setPositiveButton(R.string.str_yes) { dialogInterface, _ ->
        dialogInterface.dismiss()
        navigateToAuth()
      }
      .setNegativeButton(R.string.str_no) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .create()
      .show()
  }

  private fun showAuthCancelledAlert() {
    AlertDialog.Builder(requireContext())
      .setMessage(R.string.str_msg_auth_cancelled)
      .setPositiveButton(R.string.str_ok) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .create()
      .show()
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
  
  private fun navigateToOrderDetail(orderId: String) {
    UserOrderFragmentDirections
      .actionUserOrderFragmentToUserOrderDetailFragment(orderId)
      .apply { findNavController().navigate(this) }
  }

  private fun navigateToAuth() {
    Intent(requireActivity(), AuthActivity::class.java).apply {
      activityResultLauncher.launch(this)
    }
  }
  
  private fun navigateToPayment(order: Order) {
    Intent(requireActivity(), BrowserActivity::class.java).apply {
      this.putExtra(Constants.INTENT_EXTRA_URL, BuildConfig.PAYMENT_URL)
      this.putExtra(Constants.INTENT_EXTRA_PURPOSE, Constants.PURPOSE_PAYMENT)
      this.putExtra(Constants.INTENT_EXTRA_ARGUMENTS, order.buildPaymentArguments())
      activityResultLauncher.launch(this)
    }
  }
  
  private fun Int.buildFilterName(): String =
    when (this) {
      Constants.ORDER_STATUS_PENDING -> getString(R.string.str_status_pending)
      Constants.ORDER_STATUS_ADMIN_CONFIRMATION -> getString(R.string.str_status_waiting)
      Constants.ORDER_STATUS_ON_PROCESS -> getString(R.string.str_status_on_process)
      Constants.ORDER_STATUS_ON_DELIVERY -> getString(R.string.str_status_on_delivery)
      Constants.ORDER_STATUS_ARRIVED -> getString(R.string.str_status_arrived)
      Constants.ORDER_STATUS_FINISHED -> getString(R.string.str_status_finished)
      Constants.ORDER_STATUS_CANCELLATION_REQUEST -> getString(R.string.str_status_admin_cancel)
      Constants.ORDER_STATUS_CANCELLED -> getString(R.string.str_status_cancelled)
      else -> getString(R.string.str_status_unknown)
    }

}