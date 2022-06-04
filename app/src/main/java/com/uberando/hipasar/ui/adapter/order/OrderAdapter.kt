package com.uberando.hipasar.ui.adapter.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.*
import com.uberando.hipasar.entity.OrderType
import com.uberando.hipasar.util.Constants

class OrderAdapter(
  private val listener: OrderListener
) : ListAdapter<OrderType, RecyclerView.ViewHolder>(
  OrderComparator
) {
  
  override fun getItemViewType(position: Int): Int {
    return when (getItem(position)) {
      is OrderType.TypePending -> Constants.ORDER_STATUS_PENDING
      is OrderType.TypeAdminConfirmation -> Constants.ORDER_STATUS_ADMIN_CONFIRMATION
      is OrderType.TypeOnProcess -> Constants.ORDER_STATUS_ON_PROCESS
      is OrderType.TypeOnDelivery -> Constants.ORDER_STATUS_ON_DELIVERY
      is OrderType.TypeArrived -> Constants.ORDER_STATUS_ARRIVED
      is OrderType.TypeFinished -> Constants.ORDER_STATUS_FINISHED
      is OrderType.TypeCancellationRequest -> Constants.ORDER_STATUS_CANCELLATION_REQUEST
      is OrderType.TypeCancelled -> Constants.ORDER_STATUS_CANCELLED
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    when (viewType) {
      Constants.ORDER_STATUS_PENDING -> OrderPendingViewHolder(
        ItemOrderTypePendingBinding.inflate(
          LayoutInflater.from(parent.context), parent, false
        )
      )
      Constants.ORDER_STATUS_ADMIN_CONFIRMATION ->  OrderAdminConfirmationViewHolder(
        ItemOrderTypeAdminConfirmationBinding.inflate(
          LayoutInflater.from(parent.context), parent, false
        )
      )
      Constants.ORDER_STATUS_ON_PROCESS ->  OrderOnProcessViewHolder(
        ItemOrderTypeOnProcessBinding.inflate(
          LayoutInflater.from(parent.context), parent, false
        )
      )
      Constants.ORDER_STATUS_ON_DELIVERY ->  OrderOnDeliveryViewHolder(
        ItemOrderTypeOnDeliveryBinding.inflate(
          LayoutInflater.from(parent.context), parent, false
        )
      )
      Constants.ORDER_STATUS_ARRIVED ->  OrderArrivedViewHolder(
        ItemOrderTypeArrivedBinding.inflate(
          LayoutInflater.from(parent.context), parent, false
        )
      )
      Constants.ORDER_STATUS_FINISHED ->  OrderFinishedViewHolder(
        ItemOrderTypeFinishedBinding.inflate(
          LayoutInflater.from(parent.context), parent, false
        )
      )
      Constants.ORDER_STATUS_CANCELLATION_REQUEST ->  OrderCancellationRequestViewHolder(
        ItemOrderTypeCancellationRequestBinding.inflate(
          LayoutInflater.from(parent.context), parent, false
        )
      )
      Constants.ORDER_STATUS_CANCELLED ->  OrderCancelledViewHolder(
        ItemOrderTypeCancelledBinding.inflate(
          LayoutInflater.from(parent.context), parent, false
        )
      )
      else -> throw IllegalArgumentException("This is never called")
    }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is OrderPendingViewHolder -> {
        val item = getItem(position) as OrderType.TypePending
        holder.bind(item.order, listener)
      }
      is OrderAdminConfirmationViewHolder -> {
        val item = getItem(position) as OrderType.TypeAdminConfirmation
        holder.bind(item.order, listener)
      }
      is OrderOnProcessViewHolder -> {
        val item = getItem(position) as OrderType.TypeOnProcess
        holder.bind(item.order, listener)
      }
      is OrderOnDeliveryViewHolder -> {
        val item = getItem(position) as OrderType.TypeOnDelivery
        holder.bind(item.order, listener)
      }
      is OrderArrivedViewHolder -> {
        val item = getItem(position) as OrderType.TypeArrived
        holder.bind(item.order, listener)
      }
      is OrderFinishedViewHolder -> {
        val item = getItem(position) as OrderType.TypeFinished
        holder.bind(item.order, listener)
      }
      is OrderCancellationRequestViewHolder -> {
        val item = getItem(position) as OrderType.TypeCancellationRequest
        holder.bind(item.order, listener)
      }
      is OrderCancelledViewHolder -> {
        val item = getItem(position) as OrderType.TypeCancelled
        holder.bind(item.order, listener)
      }
    }
  }

}