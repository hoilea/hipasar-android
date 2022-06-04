package com.uberando.hipasar.ui.main.home

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.viewpager2.widget.ViewPager2
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.FragmentHomeBinding
import com.uberando.hipasar.entity.Banner
import com.uberando.hipasar.entity.Category
import com.uberando.hipasar.ui.BaseFragment
import com.uberando.hipasar.ui.adapter.banner.BannerAdapter
import com.uberando.hipasar.ui.adapter.category.CategoryAdapter
import com.uberando.hipasar.ui.adapter.product.ProductListener
import com.uberando.hipasar.ui.auth.AuthActivity
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.EventObserver
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.uberando.hipasar.ui.adapter.ClickListener
import com.uberando.hipasar.ui.order.OrderActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

  @Inject
  lateinit var viewModel: HomeViewModel

  private lateinit var categoryAdapter: CategoryAdapter

  private var afterDestroyView = false

  val bannerJob = Job()
  private val bannerScope = CoroutineScope(Dispatchers.Main)

  /**
   * Hold banner size data
   */
  private var bannerSize = 0

  /**
   * Hold banner position when view destroyed, then resume from this position when view resumed
   */
  private var bannerPosition = 0

  private val activityResultLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    handleActivityResult(result)
  }

  private val bannerAdapter by lazy {
    BannerAdapter(ClickListener { banner ->
      if (banner.categoryId != null) {
        handleBannerSelection(banner)
      } else {
        navigateToPromo()
      }
    })
  }
  
  override fun layoutResource(): Int = R.layout.fragment_home

  override fun interactWithLayout() {
    binding?.viewModel = this.viewModel
    setHasOptionsMenu(true)
    setupToolbar()
    setupViewPagerAdapter()
  }

  override fun interactWithViewModel() {
    viewModel.categories.observe(viewLifecycleOwner) { data ->
      categoryAdapter.submitList(data)
      setupViewPager(data)
    }
    viewModel.banners.observe(viewLifecycleOwner) { banners ->
      setupBannerTab(banners)
    }
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
    viewModel.whenCartContainerClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToCart()
      }
    })
    viewModel.whenCheckoutButtonClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToCheckout()
      }
    })
    viewModel.whenSearchBoxClicked.observe(viewLifecycleOwner, EventObserver { clicked ->
      if (clicked) {
        navigateToSearchQuery()
      }
    })
    viewModel.showUnauthorizedMessage.observe(viewLifecycleOwner, EventObserver { show ->
      if (show) {
        showUnauthorizedMessageAlert()
      }
    })
  }
  
  override fun onResume() {
    super.onResume()
    if (afterDestroyView) {
      afterDestroyView = false
      viewModel.requireToRefreshProduct()
    }
  }
  
  override fun onDestroyView() {
    super.onDestroyView()
    afterDestroyView = true
    binding?.homeBannerViewPager?.unregisterOnPageChangeCallback(viewPagerChangeCallback())
  }

  private fun setupToolbar() {
    interactWithMenuItemLayout { view ->
      view.setOnClickListener { navigateToCart() }
    }
  }

  private fun interactWithMenuItemLayout(action: (view: View) -> Unit) {
    binding?.homeToolbar?.menu?.run {
      findItem(R.id.item_cart).actionView.apply {
        action(this)
      }
    }
  }

  private fun setupViewPagerAdapter() {
    categoryAdapter = CategoryAdapter(
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

  private fun setupViewPager(data: List<Category>) {
    binding?.let { nonNullBinding ->
      nonNullBinding.homeViewPager.adapter = categoryAdapter
      TabLayoutMediator(nonNullBinding.homeTabLayout, nonNullBinding.homeViewPager) { tab, position ->
        tab.text = data[position].name
      }.attach()
    }
  }

  private fun setupBannerTab(banners: List<Banner>) {
    binding?.let { nonNullBinding ->
      nonNullBinding.homeBannerViewPager.adapter = bannerAdapter
      TabLayoutMediator(
        nonNullBinding.homeBannerTab,
        nonNullBinding.homeBannerViewPager
      ) { _, _ -> }.attach()
      nonNullBinding.homeBannerViewPager.registerOnPageChangeCallback(viewPagerChangeCallback())
    }
    bannerAdapter.submitList(banners)
    bannerSize = banners.size
  }

  private suspend fun setupAutoScrollBanner(size: Int) {
    withContext(Dispatchers.Main) {
      val viewPager2 = binding?.homeBannerViewPager
      delay(DELAY_TIME)
      if (bannerPosition == size - 1) {
        viewPager2?.setCurrentItem(0, true)
      } else {
        viewPager2?.setCurrentItem(bannerPosition + 1, true)
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
              }
            }
            Constants.FAILED -> {
              showAuthFailedAlert()
            }
            Constants.CANCELED -> {
              showAuthCancelledAlert()
            }
            else -> Unit
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

  private fun handleBannerSelection(banner: Banner) {
    viewModel.requireCategoryList()?.find { it.id == banner.categoryId }.let { category ->
      if (category != null) {
        viewModel.requireCategoryList()?.indexOf(category).let { index ->
          if (index != null) {
            binding?.homeViewPager?.setCurrentItem(index, true)
          } else {
            navigateToPromo()
          }
        }
      } else {
        navigateToPromo()
      }
    }
  }

  private fun viewPagerChangeCallback() = object : ViewPager2.OnPageChangeCallback() {
    override fun onPageSelected(position: Int) {
      super.onPageSelected(position)
      Timber.i("onPageSelected#$position")
      bannerScope.launch(bannerJob) { setupAutoScrollBanner(bannerSize) }
      bannerPosition = position
    }
    override fun onPageScrollStateChanged(state: Int) {
      super.onPageScrollStateChanged(state)
      Timber.i("onPageScrollStateChanged#$state")
      if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
        bannerJob.cancelChildren()
      }
    }
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

  private fun navigateToCart() {
    HomeFragmentDirections
      .actionHomeFragmentToCartFragment()
      .apply { findNavController().navigate(this) }
  }
  
  private fun navigateToProductDetail(productId: Int) {
    HomeFragmentDirections
      .actionHomeFragmentToProductDetailFragment(productId)
      .apply { findNavController().navigate(this) }
  }

  private fun navigateToPromo() {
    HomeFragmentDirections
      .actionHomeFragmentToPromoFragment()
      .apply { findNavController().navigate(this) }
  }

  private fun navigateToUserOrder() {
    findNavController().navigate(R.id.userOrderOptionFragment, null, navOptions {
      this.popUpTo(R.id.homeFragment) {
        this.inclusive = false
      }
    })
  }

  private fun navigateToSearchQuery() {
    HomeFragmentDirections
      .actionHomeFragmentToSearchQueryFragment()
      .apply { findNavController().navigate(this) }
  }

  companion object {
    private const val DELAY_TIME = 4000L
  }

}