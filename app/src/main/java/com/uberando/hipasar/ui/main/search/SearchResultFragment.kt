package com.uberando.hipasar.ui.main.search

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentSearchResultBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.adapter.product.ProductAdapter
import com.uberando.hipasar.ui.adapter.product.ProductListener
import com.uberando.hipasar.ui.auth.AuthActivity
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.EventObserver
import com.uberando.hipasar.util.asOnCartProduct
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SearchResultFragment : BaseFragment<FragmentSearchResultBinding>() {

  @Inject lateinit var viewModel: SearchResultViewModel

  private val arguments: SearchResultFragmentArgs by navArgs()

  private val activityResultLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    handleActivityResult(result)
  }

  private val recyclerViewAdapter by lazy {
    ProductAdapter(
      viewModel.cartLoadStatus,
      ProductListener(
        onProductAdded = { position, product ->
          viewModel.requireToAddProductToCart(position, product)
        },
        onProductRemoved = {
          viewModel.requireToRemoveProductFromCart(it)
        },
        onProductIncremented = { productId, amount ->
          viewModel.requireToIncrementProductFromCart(productId, amount)
        },
        onProductDecremented = { productId, amount ->
          viewModel.requireToDecrementProductFromCart(productId, amount)
        },
        onProductClicked = {
          navigateToProductDetail(it)
        },
        onProductOutOfStock = {
          MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.str_stock_not_enough)
            .setMessage(getString(R.string.str_msg_stock_not_enough))
            .setPositiveButton(R.string.str_ok) { dialog, _ ->
              dialog.dismiss()
            }
            .create()
            .show()
        }
      )
    )
  }

  override fun layoutResource() = R.layout.fragment_search_result

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    setupToolbar()
    setupRecyclerViewAdapter()
  }

  override fun interactWithViewModel() {
    viewModel.setupSearchBoxValue(arguments.query)
    viewModel.searchResult.observe(viewLifecycleOwner) { result ->
      recyclerViewAdapter.submitList(result)
    }
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    }
    viewModel.whenSearchBoxClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToSearchQuery()
      }
    })
    viewModel.whenCartChanged.observe(viewLifecycleOwner) { changed ->
      if (changed) {
        interactWithMenuItemLayout { view ->
          view.findViewById<TextView>(R.id.action_layout_cart_text).run {
            visibility = if (viewModel.requireToGetCartBadgeState()) View.VISIBLE else View.GONE
            text = viewModel.requireToGetCartBadgeCount()
          }
        }
      }
    }
//    viewModel.whenProductAddedToCart.observe(viewLifecycleOwner, EventObserver { added ->
//      if (added) {
//        Timber.i("new list; ${viewModel.requireListWithUpdatedProduct()}")
//        recyclerViewAdapter.notifyItemChanged(viewModel.requireCachedPosition(), viewModel.requireCachedProduct()?.asOnCartProduct())
//      }
//    })
    viewModel.showUnauthorizedMessage.observe(viewLifecycleOwner, EventObserver { show ->
      if (show) {
        showUnauthorizedMessageAlert()
      }
    })
  }

  private fun setupToolbar() {
    interactWithMenuItemLayout { view ->
      view.setOnClickListener { navigateToCart() }
    }
  }

  private fun setupRecyclerViewAdapter() {
    binding?.searchResultRecyclerView?.layoutManager =
      StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
    binding?.searchResultRecyclerView?.adapter = recyclerViewAdapter
  }

  private fun interactWithMenuItemLayout(action: (view: View) -> Unit) {
    binding?.searchResultToolbar?.menu?.run {
      findItem(R.id.item_cart).actionView.apply {
        action(this)
      }
    }
  }

  private fun handleActivityResult(result: ActivityResult) {
    when (result.resultCode) {
      Constants.AUTHENTICATION_REQUEST_CODE -> {
        result.data?.let { intent ->
          when (intent.getIntExtra(Constants.INTENT_EXTRA_RESULT_STATE, Constants.FAILED)) {
            Constants.SUCCESS -> {
              viewModel.requireCachedProduct()?.let {
                viewModel.requireToAddProductToCart(product = it)
                Timber.i("new list; ${viewModel.requireListWithUpdatedProduct()}")
                recyclerViewAdapter.submitList(viewModel.requireListWithUpdatedProduct())
//                recyclerViewAdapter.notifyItemChanged(viewModel.requireCachedPosition())
              }
            }
            Constants.FAILED -> {
              showAuthFailedAlert()
            }
            Constants.CANCELED -> Unit
            else -> Unit
          }
        }
      }
    }
  }

  private fun showUnauthorizedMessageAlert() {
    MaterialAlertDialogBuilder(requireContext())
      .setTitle(R.string.str_authorization_required)
      .setMessage(getString(R.string.str_msg_cart_authorization_required))
      .setPositiveButton(R.string.str_yes) { dialog, _ ->
        navigateToAuthentication()
        dialog.dismiss()
      }
      .setNegativeButton(R.string.str_no) { dialog, _ ->
        dialog.dismiss()
      }
      .create()
      .show()
  }

  private fun showAuthFailedAlert() {
    AlertDialog.Builder(requireContext())
      .setMessage(R.string.str_msg_auth_failed)
      .setPositiveButton(R.string.str_yes) { dialogInterface, _ ->
        dialogInterface.dismiss()
        navigateToAuthentication()
      }
      .setNegativeButton(R.string.str_no) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .create()
      .show()
  }

  private fun navigateToAuthentication() {
    Intent(requireActivity(), AuthActivity::class.java).apply {
      activityResultLauncher.launch(this)
    }
  }

  private fun navigateToSearchQuery() {
    SearchResultFragmentDirections
      .actionSearchResultFragmentToSearchQueryFragment()
      .apply { findNavController().navigate(this) }
  }

  private fun navigateToProductDetail(productId: Int) {
    SearchResultFragmentDirections
      .actionSearchResultFragmentToProductDetailFragment(productId)
      .apply { findNavController().navigate(this) }
  }

  private fun navigateToCart() {
    SearchResultFragmentDirections
      .actionSearchResultFragmentToCartFragment()
      .apply { findNavController().navigate(this) }
  }

}