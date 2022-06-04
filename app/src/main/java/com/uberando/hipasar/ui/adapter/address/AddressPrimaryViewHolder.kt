package com.uberando.hipasar.ui.adapter.address

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemAddressPrimaryBinding
import com.uberando.hipasar.entity.Address

class AddressPrimaryViewHolder(
  private val binding: ItemAddressPrimaryBinding
) : RecyclerView.ViewHolder(binding.root) {
  fun bind(address: Address, listener: AddressListener) {
    binding.apply {
      this.executePendingBindings()
      this.address = address
      this.listener = listener
    }
  }
}