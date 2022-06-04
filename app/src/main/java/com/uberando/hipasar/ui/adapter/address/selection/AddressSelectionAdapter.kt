package com.uberando.hipasar.ui.adapter.address.selection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.uberando.hipasar.databinding.ItemAddressSelectionBinding
import com.uberando.hipasar.entity.Address
import com.uberando.hipasar.ui.adapter.ClickListener

class AddressSelectionAdapter(
  private val listener: ClickListener<Address>
) : ListAdapter<Address, AddressSelectionViewHolder>(
  AddressSelectionComparator
) {
  
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    AddressSelectionViewHolder(
      ItemAddressSelectionBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
      ), parent.context
    )
  
  override fun onBindViewHolder(holder: AddressSelectionViewHolder, position: Int) {
    holder.bind(getItem(position), listener)
  }
  
}