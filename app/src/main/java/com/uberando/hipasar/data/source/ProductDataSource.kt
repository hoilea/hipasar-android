package com.uberando.hipasar.data.source

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.ProductRepository
import com.uberando.hipasar.data.source.remote.ProductService
import com.uberando.hipasar.entity.*
import com.uberando.hipasar.util.asProduct
import com.uberando.hipasar.util.asProductType
import timber.log.Timber
import javax.inject.Inject

class ProductDataSource @Inject constructor(
  private val productService: ProductService
) : ProductRepository {

  override suspend fun getProductByCategory(): Resource<List<Category>> {
    return try {
      productService.getProductByCategory().let { response ->
        Resource.Success(response.activeOnly())
      }
    } catch (e: Exception) {
      Resource.Failed(message = e.message.toString())
    }
  }

  override suspend fun getProductDetail(productId: Int): Resource<Product> {
    return try {
      productService.getProductDetail(productId).let { response ->
        Resource.Success(response)
      }
    } catch (e: Exception) {
      Resource.Failed(message = e.message.toString())
    }
  }

  override suspend fun getProductFeedbackAndRates(productId: Int): Resource<ProductFeedback> {
    return try {
      productService.getProductFeedbackAndRates(productId).let { response ->
        Resource.Success(response)
      }
    } catch (e: Exception) {
      Resource.Failed(message = e.message.toString())
    }
  }

  override suspend fun searchProduct(query: String): Resource<List<ProductType>> {
    return try {
      productService.searchProduct(query).let { response ->
        Resource.Success(response.asProduct().asProductType())
      }
    } catch (e: Exception) {
      Resource.Failed(message = e.message.toString())
    }
  }

  private fun List<Category>.activeOnly() =
    this.filter { it.status == null || it.status == 1 }

}