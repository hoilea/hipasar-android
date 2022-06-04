package com.uberando.hipasar.entity

sealed class ProductType {
  data class ProductAvailableType(val product: Product) : ProductType()
  data class ProductOutOfStockType(val product: Product) : ProductType()
}
