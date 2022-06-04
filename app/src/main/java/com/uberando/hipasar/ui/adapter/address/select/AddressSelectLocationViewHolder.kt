package com.uberando.hipasar.ui.adapter.address.select

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemAddressLocationBinding
import com.uberando.hipasar.entity.Kecamatan
import com.uberando.hipasar.entity.Kelurahan
import com.uberando.hipasar.ui.adapter.ClickListener

class AddressSelectLocationViewHolder(
  private val binding: ItemAddressLocationBinding
): RecyclerView.ViewHolder(binding.root) {

  fun <T> bind(data: T, listener: ClickListener<T>) {
    binding.apply {
      this.name = if (data is Kecamatan) data.name else if (data is Kelurahan) data.name else ""
      this.listener = listener
      this.executePendingBindings()
      this.frameContainer.setOnClickListener {
        listener.onItemClicked(data)
      }
    }
  }

}