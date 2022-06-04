package com.uberando.hipasar.ui.adapter.delivery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemDeliveryTimeBinding
import com.uberando.hipasar.ui.adapter.ClickListener

class DeliveryDateAdapter(
  private val listener: ClickListener<String>
) : RecyclerView.Adapter<DeliveryDateViewHolder>() {

  private val listData = arrayListOf<String>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DeliveryDateViewHolder(
    ItemDeliveryTimeBinding.inflate(
      LayoutInflater.from(parent.context), parent, false
    )
  )

  override fun onBindViewHolder(holder: DeliveryDateViewHolder, position: Int) {
    holder.bind(listData[position], listener)
  }

  override fun getItemCount() = listData.size

  fun submitListData(data: Array<String>) {
    listData.clear()
    listData.addAll(data)
  }

}