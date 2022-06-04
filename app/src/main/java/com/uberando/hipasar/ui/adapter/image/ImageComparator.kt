package com.uberando.hipasar.ui.adapter.image

import androidx.recyclerview.widget.DiffUtil
import com.uberando.hipasar.entity.Image

object ImageComparator : DiffUtil.ItemCallback<Image>() {
  
  override fun areItemsTheSame(oldItem: Image, newItem: Image) =
    oldItem.id == newItem.id
  
  override fun areContentsTheSame(oldItem: Image, newItem: Image) =
    oldItem == newItem
  
}