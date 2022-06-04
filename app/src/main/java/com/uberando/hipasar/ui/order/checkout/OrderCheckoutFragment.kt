package com.uberando.hipasar.ui.order.checkout

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.uberando.hipasar.R
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.adapter.order.product.OrderProductAdapter
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.EventObserver
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uberando.hipasar.databinding.FragmentOrderCheckoutBinding
import com.uberando.hipasar.entity.DeliveryTime
import com.uberando.hipasar.entity.PaymentMethod
import com.uberando.hipasar.ui.order.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderCheckoutFragment : BaseFragment<FragmentOrderCheckoutBinding>() {

  @Inject lateinit var orderViewModel: OrderViewModel

  @Inject lateinit var viewModel: OrderCheckoutViewModel
  
  private val orderProductAdapter by lazy {
    OrderProductAdapter()
  }
  
  private val loadingAlertDialog by lazy {
    AlertDialog.Builder(requireContext())
      .setView(R.layout.layout_loading_dialog)
      .setCancelable(false)
      .create()
  }
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    receiveSavedState<Int>(Constants.KEY_GET_ADDRESS) { selectedId ->
      viewModel.setupSelectedAddress(selectedId)
    }
    receiveSavedState<Boolean>(Constants.KEY_CREATE_ADDRESS) { afterCreate ->
      if (afterCreate) {
        viewModel.setupAfterCreate(afterCreate)
        viewModel.requireToFetchNewAddress()
      }
    }
    receiveSavedState<PaymentMethod>(Constants.KEY_GET_PAYMENT_METHOD) { paymentMethod ->
      viewModel.setupPaymentMethod(paymentMethod)
    }
    receiveSavedState<DeliveryTime>(Constants.KEY_GET_DELIVERY_TIME) { deliveryTime ->
      viewModel.setupDeliveryTime(deliveryTime)
    }
    receiveSavedState<String>(Constants.KEY_GET_DELIVERY_DATE) { deliveryDate ->
      viewModel.setupDeliveryDate(deliveryDate)
    }
  }
  
  override fun layoutResource() = R.layout.fragment_order_checkout
  
  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    setupRecyclerView()
  }
  
  override fun interactWithViewModel() {
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        requireActivity().finish()
      }
    }
    viewModel.whenChangeSelectAddressClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToChangeSelectAddress()
      }
    })
    viewModel.whenAddressNotSelected.observe(viewLifecycleOwner, EventObserver { notSelected ->
       if (notSelected) {
         MaterialAlertDialogBuilder(requireContext())
           .setTitle(R.string.str_address_not_selected)
           .setMessage(R.string.str_msg_address_not_selected)
           .setPositiveButton(R.string.str_ok) { dialogInterface, _ ->
             dialogInterface.dismiss()
             navigateToChangeSelectAddress()
           }
           .setNegativeButton(R.string.str_cancel) { dialogInterface, _ ->
             dialogInterface.dismiss()
           }
           .create()
           .show()
       }
    })
    viewModel.whenChangePaymentMethodClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToChangePaymentMethod()
      }
    })
    viewModel.whenPaymentMethodNotSelected.observe(viewLifecycleOwner, EventObserver { notSelected ->
      if (notSelected) {
        MaterialAlertDialogBuilder(requireContext())
          .setTitle(R.string.str_payment_method_not_selected)
          .setMessage(R.string.str_msg_payment_method_not_selected)
          .setPositiveButton(R.string.str_ok) { dialogInterface, _ ->
            dialogInterface.dismiss()
          }
          .create()
          .show()
      }
    })
    viewModel.whenDeliveryTimeNotSelected.observe(viewLifecycleOwner, EventObserver { notSelected ->
      if (notSelected) {
        MaterialAlertDialogBuilder(requireContext())
          .setTitle(R.string.str_delivery_time_not_selected)
          .setMessage(R.string.str_msg_delivery_time_not_selected)
          .setPositiveButton(R.string.str_ok) { dialogInterface, _ ->
            dialogInterface.dismiss()
          }
          .create()
          .show()
      }
    })
    viewModel.whenDeliveryDateNotSelected.observe(viewLifecycleOwner, EventObserver { notSelected ->
      if (notSelected) {
        MaterialAlertDialogBuilder(requireContext())
          .setTitle(R.string.str_delivery_date_not_selected)
          .setMessage(R.string.str_msg_delivery_date_not_selected)
          .setPositiveButton(R.string.str_ok) { dialogInterface, _ ->
            dialogInterface.dismiss()
          }
          .create()
          .show()
      }
    })
    viewModel.whenRequireToProcessOrder.observe(viewLifecycleOwner, EventObserver { required ->
      if (required) {
        orderViewModel.saveCreateOrderRequest(viewModel.requireCreateOrderRequest())
        determinePaymentNavigation()
      }
    })
    viewModel.whenSelectDeliveryTimeClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToSelectDeliveryTime()
      }
    })
    viewModel.whenSelectDeliveryDateClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToSelectDeliveryDate()
      }
    })
    viewModel.orderProducts.observe(viewLifecycleOwner) { products ->
      orderProductAdapter.submitList(products)
    }
    viewModel.addresses.observe(viewLifecycleOwner) { addresses ->
      if (addresses.isNotEmpty()) {
        viewModel.setupAddressSectionButtonText(getString(R.string.str_change_address))
      } else {
        viewModel.setupAddressSectionButtonText(getString(R.string.str_select_address))
      }
    }
    viewModel.showCreateOrderLoadingIndicator.observe(viewLifecycleOwner) { show ->
      if (show) {
        loadingAlertDialog.show()
      } else {
        loadingAlertDialog.dismiss()
      }
    }
  }
  
  private fun setupRecyclerView() {
    binding?.orderProductRecyclerView?.adapter = orderProductAdapter
  }
  
  private fun determinePaymentNavigation() {
    viewModel.requirePaymentRequest().let { paymentRequest ->
      when (paymentRequest.type) {
        Constants.PAYMENT_TYPE_VA -> navigateToVirtualAccountPayment()
        Constants.PAYMENT_TYPE_EWALLET -> navigateToEWalletPayment()
        Constants.PAYMENT_TYPE_COD -> {
          orderViewModel.beginOrderFlow(false)
          navigateToLoading()
        }
        else -> Unit
      }
    }
  }

  private fun navigateToChangeSelectAddress() {
    OrderCheckoutFragmentDirections
      .actionOrderCheckoutFragmentToOrderChangeSelectAddressFragment(
        title = viewModel.requireAddressSectionText(),
        addresses = viewModel.requireAddresses()
      ).apply {
        findNavController().navigate(this)
      }
  }

  private fun navigateToEWalletPayment() {
    OrderCheckoutFragmentDirections
      .actionOrderCheckoutFragmentToPaymentEWalletFragment(viewModel.requirePaymentRequest())
      .apply { findNavController().navigate(this) }
  }

  private fun navigateToVirtualAccountPayment() {
    OrderCheckoutFragmentDirections
      .actionOrderCheckoutFragmentToPaymentVAFragment(viewModel.requirePaymentRequest())
      .apply { findNavController().navigate(this) }
  }

  private fun navigateToChangePaymentMethod() {
     OrderCheckoutFragmentDirections
      .actionOrderCheckoutFragmentToPaymentMethodFragment()
      .apply { findNavController().navigate(this) }
  }

  private fun navigateToSelectDeliveryTime() {
    OrderCheckoutFragmentDirections
      .actionOrderCheckoutFragmentToSelectDeliveryTimeFragment(viewModel.requireDeliveryTimeList().toTypedArray())
      .apply { findNavController().navigate(this) }
  }

  private fun navigateToSelectDeliveryDate() {
    OrderCheckoutFragmentDirections
      .actionOrderCheckoutFragmentToSelectDeliveryDateFragment(viewModel.requireDeliveryDateList().toTypedArray())
      .apply { findNavController().navigate(this) }
  }

  private fun navigateToLoading() {
    OrderCheckoutFragmentDirections
      .actionOrderCheckoutFragmentToOrderLoadingFragment()
      .apply { findNavController().navigate(this) }
  }
  
}