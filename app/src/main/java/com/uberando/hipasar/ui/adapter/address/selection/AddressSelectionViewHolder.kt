package com.uberando.hipasar.ui.adapter.address.selection

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.R
import com.uberando.hipasar.databinding.ItemAddressSelectionBinding
import com.uberando.hipasar.entity.Address
import com.uberando.hipasar.ui.adapter.ClickListener

class AddressSelectionViewHolder(
  private val binding: ItemAddressSelectionBinding,
  private val context: Context
) : RecyclerView.ViewHolder(binding.root) {
  
  fun bind(address: Address, listener: ClickListener<Address>) {
    binding.apply {
      this.address = address
      this.listener = listener
      this.buttonEnabled = address.selectedAddress == false
      this.buttonText =
        if (address.selectedAddress) context.getString(R.string.str_selected)
        else context.getString(R.string.str_select)
    }
  }
  
}