package com.uberando.hipasar.ui.order.result

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentOrderResultBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.browser.BrowserActivity
import com.uberando.hipasar.ui.order.OrderViewModel
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class OrderResultFragment : BaseFragment<FragmentOrderResultBinding>() {
  
  @Inject lateinit var viewModel: OrderResultViewModel

  @Inject lateinit var orderViewModel: OrderViewModel
  
  private val arguments: OrderResultFragmentArgs by navArgs()
  
  override fun layoutResource() = R.layout.fragment_order_result
  
  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    handleBackButtonClick { exitFromOrderPage() }
  }
  
  override fun interactWithViewModel() {
    viewModel.setupResultCode(arguments.result)
    viewModel.setupResultContainerVisibilityState(true)
    viewModel.setupLoadingState(false)
    viewModel.whenCloseButtonClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        exitFromOrderPage()
      }
    }
    orderViewModel.requireToPayOnOtherPlatform.observe(viewLifecycleOwner, EventObserver { required ->
      if (required) {
        navigateToHandlePayment()
      }
    })
  }
  
  private fun exitFromOrderPage() {
    requireActivity().setResult(
      Constants.ORDER_REQUEST_CODE,
      Intent().apply {
        putExtra(Constants.INTENT_EXTRA_RESULT_STATE, arguments.result)
      }
    )
    requireActivity().finish()
  }

  private fun navigateToHandlePayment() {
    try {
      Intent(requireActivity(), BrowserActivity::class.java).apply {
        putExtra(Constants.INTENT_EXTRA_URL, orderViewModel.requirePaymentUrl())
        putExtra(Constants.INTENT_EXTRA_PURPOSE, Constants.PURPOSE_DEFAULT)
        startActivity(this)
      }
    } catch (e: Exception) {
      Toast.makeText(requireContext(), "Failed when parse url", Toast.LENGTH_SHORT).show()
    }
  }
  
}