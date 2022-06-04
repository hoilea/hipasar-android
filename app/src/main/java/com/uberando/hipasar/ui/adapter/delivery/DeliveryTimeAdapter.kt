package com.uberando.hipasar.ui.adapter.delivery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemDeliveryTimeBinding
import com.uberando.hipasar.entity.DeliveryTime
import com.uberando.hipasar.ui.adapter.ClickListener

class DeliveryTimeAdapter(
  private val listener: ClickListener<DeliveryTime>
) : RecyclerView.Adapter<DeliveryTimeViewHolder>() {

  private val listData = arrayListOf<DeliveryTime>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DeliveryTimeViewHolder(
    ItemDeliveryTimeBinding.inflate(
      LayoutInflater.from(parent.context), parent, false
    )
  )

  override fun onBindViewHolder(holder: DeliveryTimeViewHolder, position: Int) {
    holder.bind(listData[position], listener)
  }

  override fun getItemCount() = listData.size

  fun submitListData(data: Array<DeliveryTime>) {
    listData.clear()
    listData.addAll(data)
  }

}