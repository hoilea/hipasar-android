package com.uberando.hipasar.ui.adapter.image

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemImageBinding
import com.uberando.hipasar.entity.Image

class ImageViewHolder(
  private val binding: ItemImageBinding
) : RecyclerView.ViewHolder(binding.root) {
  
  fun bind(image: Image) {
    binding.apply {
      this.imageUrl = image.path
      this.executePendingBindings()
    }
  }
  
}