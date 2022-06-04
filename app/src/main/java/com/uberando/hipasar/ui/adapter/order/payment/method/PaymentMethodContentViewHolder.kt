package com.uberando.hipasar.ui.adapter.order.payment.method

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemPaymentMethodContentBinding
import com.uberando.hipasar.entity.PaymentMethod
import com.uberando.hipasar.ui.adapter.ClickListener

class PaymentMethodContentViewHolder(
  private val binding: ItemPaymentMethodContentBinding
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(paymentMethod: PaymentMethod, listener: ClickListener<PaymentMethod>) {
    binding.apply {
      this.listener = listener
      this.method = paymentMethod
      this.executePendingBindings()
    }
  }

}