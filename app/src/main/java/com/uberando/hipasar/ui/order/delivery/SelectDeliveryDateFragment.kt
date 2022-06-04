package com.uberando.hipasar.ui.order.delivery

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentSelectDeliveryDateBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.adapter.ClickListener
import com.uberando.hipasar.ui.adapter.delivery.DeliveryDateAdapter
import com.uberando.hipasar.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectDeliveryDateFragment : BaseFragment<FragmentSelectDeliveryDateBinding>() {

  @Inject lateinit var viewModel: SelectDeliveryDateViewModel

  private val arguments: SelectDeliveryDateFragmentArgs by navArgs()

  private val recyclerViewAdapter by lazy {
    DeliveryDateAdapter(ClickListener {
      setupSavedState(Constants.KEY_GET_DELIVERY_DATE, it)
      findNavController().navigateUp()
    })
  }

  override fun layoutResource() = R.layout.fragment_select_delivery_date

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    setupRecyclerView()
  }

  override fun interactWithViewModel() {
    arguments.deliveryDate.let {
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