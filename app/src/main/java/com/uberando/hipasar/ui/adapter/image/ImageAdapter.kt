package com.uberando.hipasar.ui.adapter.image

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemImageBinding
import com.uberando.hipasar.databinding.ItemImageSmallBinding
import com.uberando.hipasar.entity.Image

class ImageAdapter(
  private val smallImage: Boolean = false
) : ListAdapter<Image, RecyclerView.ViewHolder>(
  ImageComparator
) {
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    if (smallImage) {
      ImageSmallViewHolder(
        ItemImageSmallBinding.inflate(
          LayoutInflater.from(parent.context), parent, false
        )
      )
    } else {
      ImageViewHolder(
        ItemImageBinding.inflate(
          LayoutInflater.from(parent.context), parent, false
        )
      )
    }
  
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is ImageViewHolder -> {
        holder.bind(getItem(position))
      }
      is ImageSmallViewHolder -> {
        holder.bind(getItem(position))
      }
    }
  }
  
}