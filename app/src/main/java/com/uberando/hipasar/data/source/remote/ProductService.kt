package com.uberando.hipasar.data.source.remote

import com.uberando.hipasar.entity.Category
import com.uberando.hipasar.entity.Product
import com.uberando.hipasar.entity.ProductFeedback
import com.uberando.hipasar.entity.ProductSearch
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductService {

  @GET("products/by/categories")
  suspend fun getProductByCategory(): List<Category>

  @GET("products/{id}")
  suspend fun getProductDetail(
    @Path("id") productId: Int
  ): Product

  @GET("products/{id}/feedbacks")
  suspend fun getProductFeedbackAndRates(
    @Path("id") productId: Int
  ): ProductFeedback

  @GET("products/productsearch/{query}")
  suspend fun searchProduct(
    @Path("query") query: String
  ): List<ProductSearch>

}