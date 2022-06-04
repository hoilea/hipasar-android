package com.uberando.hipasar.ui.adapter.order.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.uberando.hipasar.databinding.ItemOrderProductBinding
import com.uberando.hipasar.entity.OrderProduct

class OrderProductAdapter : ListAdapter<OrderProduct, OrderProductViewHolder>(OrderProductComparator) {
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    OrderProductViewHolder(
      ItemOrderProductBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
      )
    )
  
  override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
    holder.bind(getItem(position))
  }
  
}