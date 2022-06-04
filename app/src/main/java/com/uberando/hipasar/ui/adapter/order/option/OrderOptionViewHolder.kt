package com.uberando.hipasar.ui.adapter.order.option

import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemOrderOptionBinding
import com.uberando.hipasar.entity.Filter
import com.uberando.hipasar.ui.adapter.ClickListener

class OrderOptionViewHolder(
  private val binding: ItemOrderOptionBinding
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(filter: Filter, listener: ClickListener<Filter>) {
    binding.apply {
      this.filter = filter
      this.listener = listener
      this.executePendingBindings()
    }
  }

}