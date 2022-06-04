package com.uberando.hipasar.ui.adapter.order.product

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemOrderProductBinding
import com.uberando.hipasar.entity.OrderProduct

class OrderProductViewHolder(
  private val binding: ItemOrderProductBinding
) : RecyclerView.ViewHolder(binding.root) {
  fun bind(data: OrderProduct) {
    binding.apply {
      this.product = data
      this.executePendingBindings()
    }
  }
}