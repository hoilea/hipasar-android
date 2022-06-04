package com.uberando.hipasar.ui.adapter.product

import androidx.recyclerview.widget.DiffUtil
import com.uberando.hipasar.entity.Product
import com.uberando.hipasar.entity.ProductType

object ProductComparator : DiffUtil.ItemCallback<ProductType>() {
  override fun areItemsTheSame(oldItem: ProductType, newItem: ProductType): Boolean =
    (oldItem is ProductType.ProductAvailableType && newItem is ProductType.ProductAvailableType && oldItem.product.id == newItem.product.id) &&
      (oldItem is ProductType.ProductOutOfStockType && newItem is ProductType.ProductOutOfStockType && oldItem.product.id == newItem.product.id)
  override fun areContentsTheSame(oldItem: ProductType, newItem: ProductType): Boolean =
    oldItem == newItem
}