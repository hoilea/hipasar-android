package com.uberando.hipasar.ui.adapter.product

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemProductBinding
import com.uberando.hipasar.entity.CartLoadStatus
import com.uberando.hipasar.entity.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class ProductViewHolder(
  private val binding: ItemProductBinding
) : RecyclerView.ViewHolder(binding.root) {

  private val scope = CoroutineScope(Dispatchers.Default)

  private var cachedPosition = 0
  private var cachedProductId = 0
  private var cachedProduct: Product? = null
  private var cachedAmount = INITIAL_AMOUNT

  fun bind(position: Int, product: Product, listener: ProductListener, cartLoadStatus: Flow<CartLoadStatus>) {
    cachedPosition = position
    cachedProductId = product.id
    cachedAmount = product.productOnCartAmount
    cachedProduct = product
    binding.apply {
      this.product = product
      this.listener = listener
      this.productOnCart = product.productOnCart
      this.executePendingBindings()
    }
    if (product.productOnCart) {
      updateAmount()
    }
    setupItemClickListener(product, listener)
    setupCartLoadStatus(cartLoadStatus)
    binding.productAmountContainer.setOnClickListener { /** Do nothing */ }
  }

  private fun setupItemClickListener(product: Product, listener: ProductListener) {
    binding.productAddButton.setOnClickListener {
      listener.addProduct(cachedPosition, cachedProduct!!)
    }
    binding.productIncreaseButton.setOnClickListener {
      if (product.stock > cachedAmount) {
        cachedAmount++
        listener.incrementProduct(cachedProductId, cachedAmount)
        updateAmount()
      } else {
        listener.notifyOutOfStock()
      }
    }
    binding.productDecreaseButton.setOnClickListener {
      if (cachedAmount == MINIMUM_AMOUNT) {
        listener.removeProduct(cachedProductId)
        clearAmount()
        setProductOnCart(false)
      } else {
        cachedAmount--
        listener.decrementProduct(cachedProductId, cachedAmount)
        updateAmount()
      }
    }
  }

  private fun setupCartLoadStatus(cartLoadStatus: Flow<CartLoadStatus>) {
    scope.launch {
      cartLoadStatus.collectLatest { loadStatus ->
        when (loadStatus) {
          is CartLoadStatus.Loading -> {
            if (loadStatus.position == cachedProductId) {
              binding.showLoadingIndicator = true
            }
          }
          is CartLoadStatus.NotLoading -> {
            if (loadStatus.position == cachedProductId) {
              binding.showLoadingIndicator = false
            }
          }
          is CartLoadStatus.Added -> {
            if (loadStatus.position == cachedProductId) {
              setProductOnCart(loadStatus.product?.productOnCart ?: true)
              cachedAmount += loadStatus.product?.productOnCartAmount?.plus(1) ?: 1
              updateAmount()
              Timber.i("onProductAdded #newAmount:$cachedAmount #productName: ${loadStatus.product?.name}")
            }
          }
        }
      }
    }
  }

  private fun setProductOnCart(onCart: Boolean) {
    binding.productOnCart = onCart
  }

  private fun updateAmount() {
    binding.amount = cachedAmount.toString()
  }

  private fun clearAmount() {
    cachedAmount = 0
  }

  companion object {
    private const val MINIMUM_AMOUNT = 1
    private const val INITIAL_AMOUNT = 0
  }

}