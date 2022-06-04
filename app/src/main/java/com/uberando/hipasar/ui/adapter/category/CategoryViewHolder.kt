package com.uberando.hipasar.ui.adapter.category

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.uberando.hipasar.databinding.ItemCategoryBinding
import com.uberando.hipasar.entity.CartLoadStatus
import com.uberando.hipasar.entity.Category
import com.uberando.hipasar.ui.adapter.product.ProductAdapter
import com.uberando.hipasar.ui.adapter.product.ProductListener
import com.uberando.hipasar.util.asProductType
import kotlinx.coroutines.flow.Flow

class CategoryViewHolder(
  private val binding: ItemCategoryBinding
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(category: Category, listener: ProductListener, loadStatus: Flow<CartLoadStatus>) {
    binding.apply {
      this.productAvailable = category.products.isNotEmpty()
      this.executePendingBindings()
    }
    category.products.isNotEmpty().let { available ->
      if (available) {
        ProductAdapter(loadStatus, listener).run {
          submitList(category.products.asProductType())
          setupRecyclerView()
        }
      }
    }
  }

  private fun ProductAdapter.setupRecyclerView() {
    binding.productRecyclerView.adapter = this
    binding.productRecyclerView.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
  }

}