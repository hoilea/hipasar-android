package com.uberando.hipasar.ui.adapter.address

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemAddressNonPrimaryBinding
import com.uberando.hipasar.entity.Address

class AddressNonPrimaryViewHolder(
  private val binding: ItemAddressNonPrimaryBinding
) : RecyclerView.ViewHolder(binding.root) {
  fun bind(address: Address, listener: AddressListener) {
    binding.apply {
      this.executePendingBindings()
      this.address = address
      this.listener = listener
    }
  }
}