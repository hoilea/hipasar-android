package com.uberando.hipasar.entity

sealed class CartLoadStatus {
  /**
   * Indicate loading state when try to add product to cart.
   */
  data class Loading(val position: Int) : CartLoadStatus()

  /**
   * Indicate finish loading status. It happened when failed when trying to add
   * product to cart.
   */
  data class NotLoading(val position: Int) : CartLoadStatus()

  /**
   * Indicate that product has been added to the cart.
   */
  data class Added(val position: Int, val product: Product?) : CartLoadStatus()
}
