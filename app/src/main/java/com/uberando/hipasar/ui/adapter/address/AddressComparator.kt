package com.uberando.hipasar.ui.adapter.address

import androidx.recyclerview.widget.DiffUtil
import com.uberando.hipasar.entity.AddressType

object AddressComparator : DiffUtil.ItemCallback<AddressType>() {
  override fun areItemsTheSame(oldItem: AddressType, newItem: AddressType) =
    (oldItem is AddressType.PrimaryType && newItem is AddressType.PrimaryType && oldItem.address.id == newItem.address.id) &&
      (oldItem is AddressType.NonPrimaryType && newItem is AddressType.NonPrimaryType && oldItem.address.id == newItem.address.id)

  override fun areContentsTheSame(oldItem: AddressType, newItem: AddressType) =
    oldItem == newItem
}