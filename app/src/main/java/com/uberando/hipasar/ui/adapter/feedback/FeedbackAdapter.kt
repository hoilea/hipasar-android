package com.uberando.hipasar.ui.adapter.feedback

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.uberando.hipasar.databinding.ItemProductFeedbackBinding
import com.uberando.hipasar.entity.ProductFeedback

class FeedbackAdapter : ListAdapter<ProductFeedback, FeedbackViewHolder>(
  FeedbackComparator
) {
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    FeedbackViewHolder(
      ItemProductFeedbackBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
      )
    )
  
  override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
    holder.bind(getItem(position))
  }
  
}