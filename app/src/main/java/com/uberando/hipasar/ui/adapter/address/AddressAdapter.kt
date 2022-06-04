package com.uberando.hipasar.ui.adapter.address

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemAddressNonPrimaryBinding
import com.uberando.hipasar.databinding.ItemAddressPrimaryBinding
import com.uberando.hipasar.entity.AddressType
import com.uberando.hipasar.util.Constants

class AddressAdapter(
  private val listener: AddressListener
) : ListAdapter<AddressType, RecyclerView.ViewHolder>(
  AddressComparator
) {

  override fun getItemViewType(position: Int): Int {
    return when (getItem(position)) {
      is AddressType.PrimaryType -> Constants.ADDRESS_PRIMARY_TYPE
      is AddressType.NonPrimaryType -> Constants.ADDRESS_NON_PRIMARY_TYPE
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      Constants.ADDRESS_PRIMARY_TYPE -> {
        AddressPrimaryViewHolder(
          ItemAddressPrimaryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
          )
        )
      }
      Constants.ADDRESS_NON_PRIMARY_TYPE -> {
        AddressNonPrimaryViewHolder(
          ItemAddressNonPrimaryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
          )
        )
      }
      else -> throw IllegalArgumentException("unknown address type")
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is AddressPrimaryViewHolder -> {
        val item = getItem(position) as AddressType.PrimaryType
        holder.bind(item.address, listener)
      }
      is AddressNonPrimaryViewHolder -> {
        val item = getItem(position) as AddressType.NonPrimaryType
        holder.bind(item.address, listener)
      }
    }
  }

}