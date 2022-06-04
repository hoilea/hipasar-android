package com.uberando.hipasar.ui.adapter.order.payment.method

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemPaymentMethodContentBinding
import com.uberando.hipasar.databinding.ItemPaymentMethodTitleBinding
import com.uberando.hipasar.entity.PaymentMethod
import com.uberando.hipasar.entity.PaymentMethodType
import com.uberando.hipasar.ui.adapter.ClickListener

class PaymentMethodAdapter(
  private val listener: ClickListener<PaymentMethod>
) : ListAdapter<PaymentMethodType, RecyclerView.ViewHolder>(
  PaymentMethodComparator
) {

  override fun getItemViewType(position: Int) =
    when (getItem(position)) {
      is PaymentMethodType.TitleType -> TYPE_TITLE
      is PaymentMethodType.ContentType -> TYPE_CONTENT
    }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    when (viewType) {
      TYPE_TITLE -> {
        PaymentMethodTitleViewHolder(
          ItemPaymentMethodTitleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
          )
        )
      }
      TYPE_CONTENT -> {
        PaymentMethodContentViewHolder(
          ItemPaymentMethodContentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
          )
        )
      }
      else -> throw IllegalArgumentException("never thrown")
    }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is PaymentMethodTitleViewHolder -> {
        val item = getItem(position) as PaymentMethodType.TitleType
        holder.bind(item.title)
      }
      is PaymentMethodContentViewHolder -> {
        val item = getItem(position) as PaymentMethodType.ContentType
        holder.bind(item.data, listener)
      }
    }
  }

  companion object {
    private const val TYPE_TITLE = 1
    private const val TYPE_CONTENT = 2
  }

}