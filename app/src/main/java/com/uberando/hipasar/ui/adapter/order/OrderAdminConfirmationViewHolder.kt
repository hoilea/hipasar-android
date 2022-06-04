package com.uberando.hipasar.ui.adapter.order

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemOrderTypeAdminConfirmationBinding
import com.uberando.hipasar.entity.Order

class OrderAdminConfirmationViewHolder(
  private val binding: ItemOrderTypeAdminConfirmationBinding
) : RecyclerView.ViewHolder(binding.root) {
  fun bind(data: Order, listener: OrderListener) {
    binding.apply {
      this.listener = listener
      this.order = data
      this.executePendingBindings()
    }
  }
}