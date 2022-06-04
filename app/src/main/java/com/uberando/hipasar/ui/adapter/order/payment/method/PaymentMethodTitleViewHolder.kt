package com.uberando.hipasar.ui.adapter.order.payment.method

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemPaymentMethodTitleBinding

class PaymentMethodTitleViewHolder(
  private val binding: ItemPaymentMethodTitleBinding
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(title: String) {
    binding.apply {
      this.methodTitle = title
      this.executePendingBindings()
    }
  }

}