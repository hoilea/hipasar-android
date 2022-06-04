package com.uberando.hipasar.ui.adapter.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uberando.hipasar.databinding.ItemProductBinding
import com.uberando.hipasar.databinding.ItemProductOutOfStockBinding
import com.uberando.hipasar.entity.CartLoadStatus
import com.uberando.hipasar.entity.ProductType
import kotlinx.coroutines.flow.Flow

class ProductAdapter(
  private val cartStatus: Flow<CartLoadStatus>,
  private val listener: ProductListener
) : ListAdapter<ProductType, RecyclerView.ViewHolder>(
  ProductComparator
) {

  override fun getItemViewType(position: Int): Int {
    return when (getItem(position)) {
      is ProductType.ProductAvailableType -> PRODUCT_AVAILABLE_TYPE
      is ProductType.ProductOutOfStockType -> PRODUCT_OUT_OF_STOCK_TYPE
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
    when (viewType) {
      PRODUCT_AVAILABLE_TYPE ->
        ProductViewHolder(ItemProductBinding.inflate(
          LayoutInflater.from(parent.context), parent, false)
        )
      PRODUCT_OUT_OF_STOCK_TYPE ->
        ProductOutOfStockViewHolder(ItemProductOutOfStockBinding.inflate(
          LayoutInflater.from(parent.context), parent, false)
        )
      else ->
        throw IllegalArgumentException("Should never thrown")
    }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is ProductViewHolder -> {
        val item = getItem(position) as ProductType.ProductAvailableType
        holder.bind(position, item.product, listener, cartStatus)
      }
      is ProductOutOfStockViewHolder -> {
        val item = getItem(position) as ProductType.ProductOutOfStockType
        holder.bind(item.product, listener)
      }
    }
  }

  companion object {
    private const val PRODUCT_AVAILABLE_TYPE = 1
    private const val PRODUCT_OUT_OF_STOCK_TYPE = 0
  }

}