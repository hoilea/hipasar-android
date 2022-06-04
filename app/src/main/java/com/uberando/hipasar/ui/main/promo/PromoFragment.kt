package com.uberando.hipasar.ui.main.promo

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentPromoBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.adapter.ClickListener
import com.uberando.hipasar.ui.adapter.product.promo.PromoProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PromoFragment : BaseFragment<FragmentPromoBinding>() {

  @Inject lateinit var viewModel: PromoViewModel

  private val productAdapter by lazy {
    PromoProductAdapter(
      ClickListener { product ->
        navigateToProductDetail(product.id)
      }
    )
  }

  override fun layoutResource(): Int = R.layout.fragment_promo

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
    viewModel.products.observe(viewLifecycleOwner) { products ->
      productAdapter.submitList(products)
    }
  }

  private fun setupRecyclerView() {
    binding?.productRecyclerView?.adapter = productAdapter
    binding?.productRecyclerView?.layoutManager = GridLayoutManager(context, 2)
  }

  private fun navigateToProductDetail(productId: Int) {
    PromoFragmentDirections
      .actionPromoFragmentToProductDetailFragment(productId)
      .apply { findNavController().navigate(this) }
  }

}