package com.uberando.hipasar.ui.adapter.product.promo

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemProductPromoBinding
import com.uberando.hipasar.entity.Product
import com.uberando.hipasar.ui.adapter.ClickListener

class PromoProductViewHolder(
  private val binding: ItemProductPromoBinding
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(product: Product, listener: ClickListener<Product>) {
    binding.apply {
      this.product = product
      this.listener = listener
      this.executePendingBindings()
    }
  }

}