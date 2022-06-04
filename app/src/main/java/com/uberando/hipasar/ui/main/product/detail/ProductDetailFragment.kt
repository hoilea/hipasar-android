package com.uberando.hipasar.ui.main.product.detail

import android.content.Intent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentProductDetailBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.adapter.feedback.FeedbackAdapter
import com.uberando.hipasar.ui.adapter.image.ImageAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.uberando.hipasar.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>() {
  
  @Inject lateinit var viewModel: ProductDetailViewModel
  
  private val arguments: ProductDetailFragmentArgs by navArgs()
  
  private val imageAdapter by lazy {
    ImageAdapter()
  }
  
  private val feedbackAdapter by lazy {
    FeedbackAdapter()
  }
  
  override fun layoutResource() = R.layout.fragment_product_detail
  
  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    setHasOptionsMenu(true)
    setupOptionMenuAction()
    setupImageTab()
    setupFeedbackRecyclerView()
  }
  
  override fun interactWithViewModel() {
    viewModel.cacheIdAndGetProduct(arguments.productId)
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    }
    viewModel.product.observe(viewLifecycleOwner) { product ->
      imageAdapter.submitList(product.images)
    }
    viewModel.productFeedback.observe(viewLifecycleOwner) { productFeedback ->
      feedbackAdapter.submitList(productFeedback)
    }
    viewModel.whenCartChanged.observe(viewLifecycleOwner) { changed ->
      if (changed) {
        interactWithMenuItemCart { view ->
          view.findViewById<TextView>(R.id.action_layout_cart_text).run {
            visibility = if (viewModel.requireToGetCartBadgeState()) View.VISIBLE else View.GONE
            text = viewModel.requireToGetCartBadgeCount()
          }
        }
      }
    }
    viewModel.showOutOfStockMessage.observe(viewLifecycleOwner, EventObserver { outOfStock ->
      if (outOfStock) {
        MaterialAlertDialogBuilder(requireContext())
          .setTitle(R.string.str_stock_not_enough)
          .setMessage(getString(R.string.str_msg_stock_not_enough))
          .setPositiveButton(R.string.str_ok) { dialog, _ ->
            dialog.dismiss()
          }
          .create()
          .show()
      }
    })
    viewModel.addProductToCartSuccess.observe(viewLifecycleOwner, EventObserver { success ->
      if (success) {
        Toast.makeText(requireContext(), getString(R.string.str_msg_add_cart_product_success), Toast.LENGTH_SHORT).show()
      } else {
        Toast.makeText(requireContext(), getString(R.string.str_msg_add_cart_product_failed), Toast.LENGTH_SHORT).show()
      }
    })
    viewModel.removeProductFromCart.observe(viewLifecycleOwner, EventObserver { success ->
      if (success) {
        Toast.makeText(requireContext(), getString(R.string.str_msg_remove_cart_product_success), Toast.LENGTH_SHORT).show()
      } else {
        Toast.makeText(requireContext(), getString(R.string.str_msg_remove_cart_product_failed), Toast.LENGTH_SHORT).show()
      }
    })
    viewModel.updateProductAmountSuccess.observe(viewLifecycleOwner, EventObserver { success ->
      if (!success) {
        Toast.makeText(requireContext(), getString(R.string.str_msg_update_cart_product_failed), Toast.LENGTH_SHORT).show()
      }
    })
  }
  
  private fun setupOptionMenuAction() {
    binding?.productDetailToolbar?.setOnMenuItemClickListener { menuItem ->
      when (menuItem.itemId) {
        R.id.item_share -> {
          handleIntentShare()
          true
        }
        else -> {
          false
        }
      }
    }
    interactWithMenuItemCart { it.setOnClickListener { navigateToCart() } }
  }

  private fun interactWithMenuItemCart(action: (view: View) -> Unit) {
    binding?.productDetailToolbar?.menu?.run {
      findItem(R.id.item_cart).actionView.apply { action(this) }
    }
  }
  
  private fun setupImageTab() {
    binding?.let { nonNullBinding ->
      nonNullBinding.productDetailViewPager.adapter = imageAdapter
      TabLayoutMediator(
        nonNullBinding.productDetailTab,
        nonNullBinding.productDetailViewPager
      ) { _, _ -> }.attach()
    }
  }
  
  private fun setupFeedbackRecyclerView() {
    binding?.productDetailFeedbackList?.adapter = feedbackAdapter
  }
  
  private fun handleIntentShare() {
    val share = Intent.createChooser(Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, "app.hipasar.com/product/${arguments.productId}")
      putExtra(Intent.EXTRA_TITLE, getString(R.string.str_hipasar_product))
      flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
      type = "text/*"
    }, null)
    startActivity(share)
  }
  
  private fun navigateToCart() {
    ProductDetailFragmentDirections
      .actionProductDetailFragmentToCartFragment()
      .apply { findNavController().navigate(this) }
  }
  
}