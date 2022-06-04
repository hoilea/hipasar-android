package com.uberando.hipasar.ui.adapter.address.selection

import androidx.recyclerview.widget.DiffUtil
import com.uberando.hipasar.entity.Address

object AddressSelectionComparator : DiffUtil.ItemCallback<Address>() {
  
  override fun areItemsTheSame(oldItem: Address, newItem: Address) =
    oldItem.id == newItem.id
  
  override fun areContentsTheSame(oldItem: Address, newItem: Address) =
    oldItem == newItem
  
}