package com.uberando.hipasar.ui.adapter.product.promo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.uberando.hipasar.databinding.ItemProductPromoBinding
import com.uberando.hipasar.entity.Product
import com.uberando.hipasar.ui.adapter.ClickListener

class PromoProductAdapter(
  private val listener: ClickListener<Product>
) : ListAdapter<Product, PromoProductViewHolder>(
  PromoProductComparator
) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    PromoProductViewHolder(
      ItemProductPromoBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
      )
    )

  override fun onBindViewHolder(holder: PromoProductViewHolder, position: Int) {
    holder.bind(getItem(position), listener)
  }

}