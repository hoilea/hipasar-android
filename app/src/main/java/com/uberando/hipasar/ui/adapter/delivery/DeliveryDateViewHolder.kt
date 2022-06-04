package com.uberando.hipasar.ui.adapter.delivery

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemDeliveryTimeBinding
import com.uberando.hipasar.ui.adapter.ClickListener

class DeliveryDateViewHolder(
  private val binding: ItemDeliveryTimeBinding
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(data: String, listener: ClickListener<String>) {
    binding.apply {
      this.name = data
      this.executePendingBindings()
      this.frameContainer.setOnClickListener {
        listener.onItemClicked(data)
      }
    }
  }

}