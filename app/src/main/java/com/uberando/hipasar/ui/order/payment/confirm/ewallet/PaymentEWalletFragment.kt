package com.uberando.hipasar.ui.order.payment.confirm.ewallet

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentPaymentEWalletBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.order.OrderViewModel
import com.uberando.hipasar.ui.order.payment.confirm.PaymentConfirmViewModel
import com.uberando.hipasar.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PaymentEWalletFragment : BaseFragment<FragmentPaymentEWalletBinding>() {

  @Inject lateinit var paymentConfirmViewModel: PaymentConfirmViewModel

  @Inject lateinit var orderViewModel: OrderViewModel

  private val arguments: PaymentEWalletFragmentArgs by navArgs()

  override fun layoutResource() = R.layout.fragment_payment_e_wallet

  override fun interactWithLayout() {
    binding?.viewModel = this.paymentConfirmViewModel
  }

  override fun interactWithViewModel() {
    handleBackButtonClick { paymentConfirmViewModel.onCloseButtonClick() }
    paymentConfirmViewModel.setupPaymentRequest(arguments.paymentRequest)
    paymentConfirmViewModel.whenCloseButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        MaterialAlertDialogBuilder(requireContext())
          .setTitle(R.string.str_cancel_payment)
          .setMessage(R.string.str_msg_cancel_payment)
          .setPositiveButton(R.string.str_ok) { dialogInterface, _ ->
            dialogInterface.dismiss()
            exitFromOrderPage()
          }
          .setNegativeButton(R.string.str_cancel) { dialogInterface, _ ->
            dialogInterface.dismiss()
          }
          .create()
          .show()
      }
    })
    paymentConfirmViewModel.showInvalidInputMessage.observe(viewLifecycleOwner, EventObserver { show ->
      if (show) {
        MaterialAlertDialogBuilder(requireContext())
          .setTitle(R.string.str_invalid_phone_input)
          .setMessage(R.string.str_msg_phone_input)
          .setPositiveButton(R.string.str_ok) { dialogInterface, _ ->
            dialogInterface.dismiss()
          }
          .create()
          .show()
      }
    })
    paymentConfirmViewModel.whenOrderDataAccepted.observe(viewLifecycleOwner, EventObserver { accepted ->
      if (accepted) {
        orderViewModel.savePaymentRequest(paymentConfirmViewModel.requirePaymentRequest())
        orderViewModel.beginOrderFlow(true)
        navigateToLoading()
      }
    })
  }

  private fun exitFromOrderPage() {
    requireActivity().finish()
  }

  private fun navigateToLoading() {
    PaymentEWalletFragmentDirections
      .actionPaymentEWalletFragmentToOrderLoadingFragment()
      .apply { findNavController().navigate(this) }
  }

}