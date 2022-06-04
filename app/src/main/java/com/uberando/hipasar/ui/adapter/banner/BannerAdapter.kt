package com.uberando.hipasar.ui.adapter.banner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.uberando.hipasar.databinding.ItemBannerBinding
import com.uberando.hipasar.entity.Banner
import com.uberando.hipasar.ui.adapter.ClickListener

class BannerAdapter(
  private val listener: ClickListener<Banner>
) : ListAdapter<Banner, BannerViewHolder>(BannerComparator) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    BannerViewHolder(
      ItemBannerBinding.inflate(
        LayoutInflater.from(parent.context), parent ,false
      )
    )

  override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
    holder.bind(getItem(position), listener)
  }

}