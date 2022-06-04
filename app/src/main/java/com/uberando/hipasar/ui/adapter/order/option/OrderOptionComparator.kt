package com.uberando.hipasar.ui.adapter.order.option

import androidx.recyclerview.widget.DiffUtil
import com.uberando.hipasar.entity.Filter

object OrderOptionComparator : DiffUtil.ItemCallback<Filter>() {

  override fun areItemsTheSame(oldItem: Filter, newItem: Filter) =
    oldItem.code == newItem.code

  override fun areContentsTheSame(oldItem: Filter, newItem: Filter) =
    oldItem == newItem

}