package com.uberando.hipasar.ui.adapter.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.uberando.hipasar.databinding.ItemCategoryBinding
import com.uberando.hipasar.entity.CartLoadStatus
import com.uberando.hipasar.entity.Category
import com.uberando.hipasar.ui.adapter.product.ProductListener
import kotlinx.coroutines.flow.Flow

class CategoryAdapter(
  private val loadStatus: Flow<CartLoadStatus>,
  private val productListener: ProductListener
) : ListAdapter<Category, CategoryViewHolder>(
  CategoryComparator
) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder =
    CategoryViewHolder(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

  override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
    holder.bind(getItem(position), productListener, loadStatus)
  }

}