package com.uberando.hipasar.ui.adapter.delivery

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemDeliveryTimeBinding
import com.uberando.hipasar.entity.DeliveryTime
import com.uberando.hipasar.ui.adapter.ClickListener
import com.uberando.hipasar.util.buildSingleLineText

class DeliveryTimeViewHolder(
  private val binding: ItemDeliveryTimeBinding
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(data: DeliveryTime, listener: ClickListener<DeliveryTime>) {
    binding.apply {
      this.name = data.buildSingleLineText()
      this.executePendingBindings()
      this.frameContainer.setOnClickListener {
        listener.onItemClicked(data)
      }
    }
  }

}