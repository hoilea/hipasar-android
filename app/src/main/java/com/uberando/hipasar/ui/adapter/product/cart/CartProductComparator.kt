package com.uberando.hipasar.ui.adapter.product.cart

import androidx.recyclerview.widget.DiffUtil
import com.uberando.hipasar.entity.CartProduct

object CartProductComparator : DiffUtil.ItemCallback<CartProduct>() {
  override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean =
    oldItem.id == newItem.id
  override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean =
    oldItem == newItem
}