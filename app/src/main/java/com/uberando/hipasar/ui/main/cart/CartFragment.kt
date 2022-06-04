package com.uberando.hipasar.ui.main.cart

import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentCartBinding
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.adapter.product.ProductListener
import com.uberando.hipasar.ui.adapter.product.cart.CartProductAdapter
import com.uberando.hipasar.ui.auth.AuthActivity
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.EventObserver
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.uberando.hipasar.ui.order.OrderActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>() {

  @Inject
  lateinit var viewModel: CartViewModel

  private val cartProductAdapter: CartProductAdapter by lazy {
    CartProductAdapter(
      ProductListener(
        onProductAdded = { _, _ -> },
        onProductRemoved = {
          askQuestionFirst {
            viewModel.requireToRemoveProductFromCart(it)
          }
        },
        onProductIncremented = { productId, amount ->
          viewModel.requireToUpdateProductAmountFromCart(productId, amount)
        },
        onProductDecremented = { productId, amount ->
          viewModel.requireToUpdateProductAmountFromCart(productId, amount)
        },
        onProductClicked = {
          Timber.i("onProductClicked: id: $it")
        },
        onProductOutOfStock = {

        }
      )
    )
  }
  
  private val activityResultLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    handleActivityResult(result)
  }

  override fun layoutResource(): Int = R.layout.fragment_cart

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    setupRecyclerView()
  }

  override fun interactWithViewModel() {
    viewModel.cartProducts.observe(viewLifecycleOwner) { products ->
      viewModel.onCartProductChanged()
      cartProductAdapter.submitList(products)
    }
    viewModel.whenBackButtonClicked.observe(viewLifecycleOwner) { clicked ->
      if (clicked) {
        findNavController().navigateUp()
      }
    }
    viewModel.whenLoginButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToAuthentication()
      }
    })
    viewModel.whenCheckoutButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToCheckout()
      }
    })
    viewModel.removeProductSuccess.observe(viewLifecycleOwner, EventObserver { success ->
      if (success) {
        Toast.makeText(requireContext(), getString(R.string.str_msg_remove_cart_product_success), Toast.LENGTH_SHORT).show()
      } else {
        Toast.makeText(requireContext(), getString(R.string.str_msg_remove_cart_product_failed), Toast.LENGTH_SHORT).show()
      }
    })
    viewModel.updateProductAmountFailed.observe(viewLifecycleOwner, EventObserver { failed ->
      if (failed) {
        Toast.makeText(requireContext(), getString(R.string.str_msg_update_cart_product_failed), Toast.LENGTH_SHORT).show()
      }
    })
  }

  private fun setupRecyclerView() {
    binding?.cartProductList?.adapter = cartProductAdapter
  }
  
  private fun handleActivityResult(result: ActivityResult) {
    when (result.resultCode) {
      Constants.AUTHENTICATION_REQUEST_CODE -> {
        result.data?.let { intent ->
          when (intent.getIntExtra(Constants.INTENT_EXTRA_RESULT_STATE, Constants.FAILED)) {
            Constants.SUCCESS -> {
              navigateToCheckout()
            }
            Constants.FAILED -> {
              showAuthFailedAlert()
            }
            Constants.CANCELED -> {
              showAuthCancelledAlert()
            }
          }
        }
      }
      Constants.ORDER_REQUEST_CODE -> {
        result.data?.let { intent ->
          when (intent.getStringExtra(Constants.INTENT_EXTRA_RESULT_STATE)) {
            Constants.CALLBACK_SUCCESS -> {
              navigateToUserOrder()
            }
            Constants.CALLBACK_FAILED -> Unit
          }
        }
      }
    }
  }

  private fun askQuestionFirst(action: () -> Unit) {
    MaterialAlertDialogBuilder(requireContext())
      .setTitle(R.string.str_remove_product)
      .setMessage(getString(R.string.str_msg_cart_remove_product))
      .setPositiveButton(R.string.str_yes) { dialog, _ ->
        action()
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
  
  private fun showAuthCancelledAlert() {
    AlertDialog.Builder(requireContext())
      .setMessage(R.string.str_msg_auth_cancelled)
      .setPositiveButton(R.string.str_ok) { dialogInterface, _ ->
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
  
  private fun navigateToCheckout() {
    Intent(requireContext(), OrderActivity::class.java).apply {
      activityResultLauncher.launch(this)
    }
  }

  private fun navigateToUserOrder() {
    findNavController().navigate(R.id.userOrderOptionFragment, null, navOptions {
      this.popUpTo(R.id.homeFragment) {
        this.inclusive = false
      }
    })
  }

}