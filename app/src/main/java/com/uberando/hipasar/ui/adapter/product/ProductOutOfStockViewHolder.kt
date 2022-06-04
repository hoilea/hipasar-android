package com.uberando.hipasar.ui.adapter.product

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemProductOutOfStockBinding
import com.uberando.hipasar.entity.Product

class ProductOutOfStockViewHolder(
  private val binding: ItemProductOutOfStockBinding
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(product: Product, listener: ProductListener) {
    binding.apply {
      this.product = product
      this.listener = listener
      this.executePendingBindings()
    }
  }

}