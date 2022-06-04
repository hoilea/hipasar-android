package com.uberando.hipasar.ui.main.order.option

import androidx.navigation.fragment.findNavController
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentUserOrderOptionBinding
import com.uberando.hipasar.entity.Filter
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.adapter.ClickListener
import com.uberando.hipasar.ui.adapter.order.option.OrderOptionAdapter
import com.uberando.hipasar.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserOrderOptionFragment : BaseFragment<FragmentUserOrderOptionBinding>() {
  
  @Inject lateinit var viewModel: UserOrderOptionViewModel

  private val recyclerViewAdapter by lazy {
    OrderOptionAdapter(ClickListener { filter ->
      navigateToOrder(filter)
    })
  }
  
  override fun layoutResource() = R.layout.fragment_user_order_option
  
  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    setupRecyclerView()
  }
  
  override fun interactWithViewModel() {
    viewModel.orderFiltersReady.observe(viewLifecycleOwner, EventObserver { ready ->
      if (ready) {
        recyclerViewAdapter.submitList(viewModel.getFilters())
      }
    })
  }

  private fun setupRecyclerView() {
    binding?.orderOptionList?.adapter = recyclerViewAdapter
  }

  private fun navigateToOrder(filter: Filter) {
    UserOrderOptionFragmentDirections
      .actionUserOrderOptionFragmentToUserOrderFragment(filter)
      .apply { findNavController().navigate(this) }
  }
  
}