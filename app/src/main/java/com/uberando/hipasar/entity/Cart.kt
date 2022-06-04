package com.uberando.hipasar.entity

data class Cart(
  val finalPrice: Int,
  val products: List<CartProduct>
)
