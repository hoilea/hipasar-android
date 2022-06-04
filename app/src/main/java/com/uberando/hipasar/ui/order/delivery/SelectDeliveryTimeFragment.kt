package com.uberando.hipasar.ui.order.delivery

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentSelectDeliveryTimeBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.adapter.ClickListener
import com.uberando.hipasar.ui.adapter.delivery.DeliveryTimeAdapter
import com.uberando.hipasar.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectDeliveryTimeFragment : BaseFragment<FragmentSelectDeliveryTimeBinding>() {

  @Inject lateinit var viewModel: SelectDeliveryTimeViewModel

  private val arguments: SelectDeliveryTimeFragmentArgs by navArgs()

  private val recyclerViewAdapter by lazy {
    DeliveryTimeAdapter(ClickListener {
      setupSavedState(Constants.KEY_GET_DELIVERY_TIME, it)
      findNavController().navigateUp()
    })
  }

  override fun layoutResource(): Int = R.layout.fragment_select_delivery_time

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    setupRecyclerView()
  }

  override fun interactWithViewModel() {
    arguments.deliveryTime.let {
      recyclerViewAdapter.submitListData(it)
    }
    viewModel.whenNavigationIconClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    }
  }

  private fun setupRecyclerView() {
    binding?.deliveryTimeRecyclerView?.adapter = recyclerViewAdapter
  }

}