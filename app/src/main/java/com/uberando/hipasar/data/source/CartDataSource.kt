package com.uberando.hipasar.data.source

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.CartRepository
import com.uberando.hipasar.data.source.local.dao.AccountDao
import com.uberando.hipasar.data.source.remote.CartService
import com.uberando.hipasar.entity.Cart
import com.uberando.hipasar.entity.Product
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.asCartProductRequest
import com.uberando.hipasar.util.asUpdateCartQuantityRequest
import com.uberando.hipasar.util.makeBearerToken
import timber.log.Timber
import javax.inject.Inject

class CartDataSource @Inject constructor(
  private val accountDao: AccountDao,
  private val cartService: CartService
) : CartRepository {

  override suspend fun getCart(): Resource<Cart> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          cartService.getCartProducts(token).let { response ->
            Resource.Success(Cart(0, response))
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Timber.e(e)
      Resource.Failed()
    }
  }

  override suspend fun insertProduct(product: Product): Resource<Nothing> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          cartService.addCartProduct(token, product.asCartProductRequest()).let {
            Resource.Success()
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun deleteProduct(productId: Int): Resource<Nothing> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          cartService.deleteCartProduct(token, productId).let {
            Resource.Success()
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun updateProductAmount(productId: Int, amount: Int): Resource<Nothing> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          cartService.updateQuantity(token, productId, amount.asUpdateCartQuantityRequest()).let {
            Resource.Success()
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun clearCart(): Resource<Nothing> {
    return try {
      Resource.Success()
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  private suspend fun getTokenOrNull(): String? = accountDao.getSingleAccount()?.token?.makeBearerToken()

}