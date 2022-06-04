package com.uberando.hipasar.ui.adapter.product.promo

import androidx.recyclerview.widget.DiffUtil
import com.uberando.hipasar.entity.Product

object PromoProductComparator : DiffUtil.ItemCallback<Product>() {

  override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
    return oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
    return oldItem == newItem
  }

}