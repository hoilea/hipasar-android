package com.uberando.hipasar.ui.adapter.order.payment.method

import androidx.recyclerview.widget.DiffUtil
import com.uberando.hipasar.entity.PaymentMethodType

object PaymentMethodComparator : DiffUtil.ItemCallback<PaymentMethodType>() {

  override fun areItemsTheSame(oldItem: PaymentMethodType, newItem: PaymentMethodType) =
    (oldItem is PaymentMethodType.TitleType && newItem is PaymentMethodType.TitleType && oldItem.title == newItem.title) &&
      (oldItem is PaymentMethodType.ContentType && newItem is PaymentMethodType.ContentType && oldItem.data.id == newItem.data.id)

  override fun areContentsTheSame(oldItem: PaymentMethodType, newItem: PaymentMethodType) =
    oldItem == newItem

}