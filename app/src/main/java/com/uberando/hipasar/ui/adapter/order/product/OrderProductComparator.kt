package com.uberando.hipasar.ui.adapter.order.product

import androidx.recyclerview.widget.DiffUtil
import com.uberando.hipasar.entity.OrderProduct

object OrderProductComparator : DiffUtil.ItemCallback<OrderProduct>() {
  override fun areItemsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean =
    oldItem.id == newItem.id
  override fun areContentsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean =
    oldItem == newItem
}