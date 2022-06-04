package com.uberando.hipasar.ui.adapter.banner

import androidx.recyclerview.widget.DiffUtil
import com.uberando.hipasar.entity.Banner

object BannerComparator : DiffUtil.ItemCallback<Banner>() {

  override fun areItemsTheSame(oldItem: Banner, newItem: Banner) =
    oldItem.id == newItem.id

  override fun areContentsTheSame(oldItem: Banner, newItem: Banner) =
    oldItem == newItem

}