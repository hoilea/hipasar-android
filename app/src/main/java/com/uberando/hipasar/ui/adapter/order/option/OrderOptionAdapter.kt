package com.uberando.hipasar.ui.adapter.order.option

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.uberando.hipasar.databinding.ItemOrderOptionBinding
import com.uberando.hipasar.entity.Filter
import com.uberando.hipasar.ui.adapter.ClickListener

class OrderOptionAdapter(
  private val listener: ClickListener<Filter>
) : ListAdapter<Filter, OrderOptionViewHolder>(
  OrderOptionComparator
) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    OrderOptionViewHolder(
      ItemOrderOptionBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
      )
    )

  override fun onBindViewHolder(holder: OrderOptionViewHolder, position: Int) {
    holder.bind(getItem(position), listener)
  }

}