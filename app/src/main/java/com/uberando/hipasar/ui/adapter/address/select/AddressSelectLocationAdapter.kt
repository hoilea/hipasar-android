package com.uberando.hipasar.ui.adapter.address.select

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemAddressLocationBinding
import com.uberando.hipasar.ui.adapter.ClickListener

class AddressSelectLocationAdapter<T>(
  private val listener: ClickListener<T>
) : RecyclerView.Adapter<AddressSelectLocationViewHolder>() {

  private val listData: MutableList<T> = mutableListOf()

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ) = AddressSelectLocationViewHolder(
    ItemAddressLocationBinding.inflate(
      LayoutInflater.from(parent.context), parent, false
    )
  )

  override fun onBindViewHolder(holder: AddressSelectLocationViewHolder, position: Int) {
    holder.bind(listData[position], listener)
  }

  override fun getItemCount() = listData.size

  fun submitListData(data: List<T>) {
    listData.clear()
    listData.addAll(data)
  }

}