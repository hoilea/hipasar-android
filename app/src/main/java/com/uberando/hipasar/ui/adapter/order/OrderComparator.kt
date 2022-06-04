package com.uberando.hipasar.ui.adapter.order

import androidx.recyclerview.widget.DiffUtil
import com.uberando.hipasar.entity.OrderType

object OrderComparator : DiffUtil.ItemCallback<OrderType>() {
  
  override fun areItemsTheSame(oldItem: OrderType, newItem: OrderType) =
    (oldItem is OrderType.TypePending && newItem is OrderType.TypePending && oldItem.order.id == newItem.order.id) &&
    (oldItem is OrderType.TypeAdminConfirmation && newItem is OrderType.TypeAdminConfirmation && oldItem.order.id == newItem.order.id) &&
    (oldItem is OrderType.TypeOnProcess && newItem is OrderType.TypeOnProcess && oldItem.order.id == newItem.order.id) &&
    (oldItem is OrderType.TypeOnDelivery && newItem is OrderType.TypeOnDelivery && oldItem.order.id == newItem.order.id) &&
    (oldItem is OrderType.TypeArrived && newItem is OrderType.TypeArrived && oldItem.order.id == newItem.order.id) &&
    (oldItem is OrderType.TypeFinished && newItem is OrderType.TypeFinished && oldItem.order.id == newItem.order.id) &&
    (oldItem is OrderType.TypeCancellationRequest && newItem is OrderType.TypeCancellationRequest && oldItem.order.id == newItem.order.id) &&
    (oldItem is OrderType.TypeCancelled && newItem is OrderType.TypeCancelled && oldItem.order.id == newItem.order.id)
  
  override fun areContentsTheSame(oldItem: OrderType, newItem: OrderType) =
    oldItem == newItem
  
}