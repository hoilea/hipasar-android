package com.uberando.hipasar.ui.adapter.feedback

import androidx.recyclerview.widget.DiffUtil
import com.uberando.hipasar.entity.ProductFeedback

object FeedbackComparator : DiffUtil.ItemCallback<ProductFeedback>() {
  
  override fun areItemsTheSame(oldItem: ProductFeedback, newItem: ProductFeedback) =
    oldItem.id == newItem.id
  
  override fun areContentsTheSame(oldItem: ProductFeedback, newItem: ProductFeedback) =
    oldItem == newItem
  
}