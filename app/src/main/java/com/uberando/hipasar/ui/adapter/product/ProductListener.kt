package com.uberando.hipasar.ui.adapter.product

import com.uberando.hipasar.entity.Product

class ProductListener(
  val onProductAdded: (position: Int, product: Product) -> Unit,
  val onProductRemoved: (productId: Int) -> Unit,
  val onProductIncremented: (productId: Int, amount: Int) -> Unit,
  val onProductDecremented: (productId: Int, amount: Int) -> Unit,
  val onProductClicked: (productId: Int) -> Unit,
  val onProductOutOfStock: () -> Unit
) {
  fun addProduct(position: Int, product: Product) = onProductAdded(position, product)
  fun removeProduct(productId: Int) = onProductRemoved(productId)
  fun incrementProduct(productId: Int, amount: Int) = onProductIncremented(productId, amount)
  fun decrementProduct(productId: Int, amount: Int) = onProductDecremented(productId, amount)
  fun clickProduct(productId: Int) = onProductClicked(productId)
  fun notifyOutOfStock() = onProductOutOfStock()
}