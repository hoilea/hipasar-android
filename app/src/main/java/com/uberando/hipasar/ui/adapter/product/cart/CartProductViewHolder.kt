package com.uberando.hipasar.ui.adapter.product.cart

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemCartProductBinding
import com.uberando.hipasar.entity.CartProduct
import com.uberando.hipasar.ui.adapter.product.ProductListener

class CartProductViewHolder(
  private val binding: ItemCartProductBinding
) : RecyclerView.ViewHolder(binding.root) {

  var cachedProductId = 0
  var cachedAmount = 0

  fun bind(product: CartProduct, listener: ProductListener) {
    cachedProductId = product.id
    cachedAmount = product.quantity
    binding.apply {
      this.product = product
      this.listener = listener
      this.executePendingBindings()
    }
    updateAmount()
    setupItemClickListener(listener)
  }

  private fun setupItemClickListener(listener: ProductListener) {
    binding.productIncreaseButton.setOnClickListener {
      cachedAmount++
      listener.incrementProduct(cachedProductId, cachedAmount)
      updateAmount()
    }
    binding.productDecreaseButton.setOnClickListener {
      if (cachedAmount == MINIMUM_AMOUNT) {
        listener.removeProduct(cachedProductId)
      } else {
        cachedAmount--
        listener.decrementProduct(cachedProductId, cachedAmount)
        updateAmount()
      }
    }
  }

  private fun updateAmount() {
    binding.amount = cachedAmount.toString()
  }

  companion object {
    private const val MINIMUM_AMOUNT = 1
  }

}