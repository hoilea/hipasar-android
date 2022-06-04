package com.uberando.hipasar.ui.adapter.order

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemOrderTypeArrivedBinding
import com.uberando.hipasar.entity.Order

class OrderArrivedViewHolder(
  private val binding: ItemOrderTypeArrivedBinding
) : RecyclerView.ViewHolder(binding.root) {
  fun bind(data: Order, listener: OrderListener) {
    binding.apply {
      this.listener = listener
      this.order = data
      this.executePendingBindings()
    }
  }
}