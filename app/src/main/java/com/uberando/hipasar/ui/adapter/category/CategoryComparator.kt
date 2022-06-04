package com.uberando.hipasar.ui.adapter.category

import androidx.recyclerview.widget.DiffUtil
import com.uberando.hipasar.entity.Category

object CategoryComparator : DiffUtil.ItemCallback<Category>() {
  override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
    oldItem.id == newItem.id
  override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
    oldItem == newItem
}