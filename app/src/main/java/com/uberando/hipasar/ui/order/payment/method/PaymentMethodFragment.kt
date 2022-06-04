package com.uberando.hipasar.ui.order.payment.method

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentPaymentMethodBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.adapter.ClickListener
import com.uberando.hipasar.ui.adapter.order.payment.method.PaymentMethodAdapter
import com.uberando.hipasar.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PaymentMethodFragment : BaseFragment<FragmentPaymentMethodBinding>() {

  @Inject lateinit var viewModel: PaymentMethodViewModel

  private val paymentMethodAdapter by lazy {
    PaymentMethodAdapter(
      ClickListener {
        setupSavedState(Constants.KEY_GET_PAYMENT_METHOD, it)
        findNavController().navigateUp()
      }
    )
  }

  override fun layoutResource() = R.layout.fragment_payment_method

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    setupRecyclerView()
  }

  override fun interactWithViewModel() {
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    }
    viewModel.paymentMethodTypes.observe(viewLifecycleOwner) { data ->
      paymentMethodAdapter.submitList(data)
    }
  }

  private fun setupRecyclerView() {
    binding?.paymentMethodList?.adapter = paymentMethodAdapter
  }

  private fun showComingSoonMessage() {
    Toast.makeText(requireContext(), getString(R.string.str_msg_cod_coming_soon), Toast.LENGTH_SHORT).show()
  }

}