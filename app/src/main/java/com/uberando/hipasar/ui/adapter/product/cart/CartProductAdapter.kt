package com.uberando.hipasar.ui.adapter.product.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.uberando.hipasar.databinding.ItemCartProductBinding
import com.uberando.hipasar.entity.CartProduct
import com.uberando.hipasar.ui.adapter.product.ProductListener

class CartProductAdapter(
  private val listener: ProductListener
) : ListAdapter<CartProduct, CartProductViewHolder>(
  CartProductComparator
) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    CartProductViewHolder(
      ItemCartProductBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
      )
    )

  override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
    holder.bind(getItem(position), listener)
  }

}