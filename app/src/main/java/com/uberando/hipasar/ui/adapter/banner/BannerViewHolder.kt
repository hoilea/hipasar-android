package com.uberando.hipasar.ui.adapter.banner

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemBannerBinding
import com.uberando.hipasar.entity.Banner
import com.uberando.hipasar.ui.adapter.ClickListener

class BannerViewHolder(
  private val binding: ItemBannerBinding
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(data: Banner, listener: ClickListener<Banner>) {
    binding.apply {
      this.listener = listener
      this.banner = data
      this.executePendingBindings()
    }
  }

}