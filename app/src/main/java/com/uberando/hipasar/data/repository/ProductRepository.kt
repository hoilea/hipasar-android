package com.uberando.hipasar.data.repository

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.entity.*

interface ProductRepository {
  suspend fun getProductByCategory(): Resource<List<Category>>
  suspend fun getProductDetail(productId: Int): Resource<Product>
  suspend fun getProductFeedbackAndRates(productId: Int): Resource<ProductFeedback>
  suspend fun searchProduct(query: String): Resource<List<ProductType>>
}