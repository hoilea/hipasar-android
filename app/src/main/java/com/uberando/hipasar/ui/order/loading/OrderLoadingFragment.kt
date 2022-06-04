package com.uberando.hipasar.ui.order.loading

import androidx.navigation.fragment.findNavController
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentOrderLoadingBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.order.OrderViewModel
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderLoadingFragment : BaseFragment<FragmentOrderLoadingBinding>() {

  @Inject lateinit var orderViewModel: OrderViewModel

  override fun layoutResource() = R.layout.fragment_order_loading

  override fun interactWithLayout() {
    handleBackButtonClick {
      // Do nothing
    }
  }

  override fun interactWithViewModel() {
    orderViewModel.requestPaymentSuccess.observe(viewLifecycleOwner, EventObserver { success ->
       if (success) {
         navigateToResult(Constants.CALLBACK_SUCCESS)
       } else {
         navigateToResult(Constants.CALLBACK_FAILED)
       }
    })
    orderViewModel.createOrderSuccess.observe(viewLifecycleOwner, EventObserver { success ->
      if (!success) {
        navigateToResult(Constants.CALLBACK_FAILED)
      }
    })
  }

  private fun navigateToResult(result: String) {
    OrderLoadingFragmentDirections
      .actionOrderLoadingFragmentToOrderResultFragment(result)
      .apply { findNavController().navigate(this) }
  }

}