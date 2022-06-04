package com.uberando.hipasar.ui.adapter.feedback

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemProductFeedbackBinding
import com.uberando.hipasar.entity.ProductFeedback
import com.uberando.hipasar.ui.adapter.image.ImageAdapter

class FeedbackViewHolder(
  private val binding: ItemProductFeedbackBinding
) : RecyclerView.ViewHolder(binding.root) {
  
  fun bind(feedback: ProductFeedback) {
    binding.apply {
      this.feedback = feedback
      this.imageAvailable = feedback.images.isNotEmpty()
      this.executePendingBindings()
    }
    feedback.images.isNotEmpty().let { notEmpty ->
      if (notEmpty) {
        initializeImageAdapter().submitList(feedback.images)
      }
    }
  }
  
  private fun initializeImageAdapter() =
    ImageAdapter(true).also {
      binding.productFeedbackImageList.adapter = it
    }
  
}