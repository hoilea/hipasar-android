package com.uberando.hipasar.ui.adapter.image

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemImageSmallBinding
import com.uberando.hipasar.entity.Image

class ImageSmallViewHolder(
  private val binding: ItemImageSmallBinding
) : RecyclerView.ViewHolder(binding.root) {
  
  fun bind(image: Image) {
    binding.apply {
      this.imageUrl = image.path
      this.executePendingBindings()
    }
  }
  
}