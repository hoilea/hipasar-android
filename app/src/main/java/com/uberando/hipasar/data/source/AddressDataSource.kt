package com.uberando.hipasar.data.source

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.AddressRepository
import com.uberando.hipasar.data.source.dummy.DummyKecamatan
import com.uberando.hipasar.data.source.local.dao.AccountDao
import com.uberando.hipasar.data.source.remote.AddressService
import com.uberando.hipasar.data.source.remote.IndonesianAddressService
import com.uberando.hipasar.di.OtherApi
import com.uberando.hipasar.entity.Address
import com.uberando.hipasar.entity.Kecamatan
import com.uberando.hipasar.entity.Kelurahan
import com.uberando.hipasar.entity.request.address.CreateAddressRequest
import com.uberando.hipasar.util.Constants
import com.uberando.hipasar.util.makeBearerToken
import javax.inject.Inject

class AddressDataSource @Inject constructor(
  @OtherApi private val indonesianAddressService: IndonesianAddressService,
  private val addressService: AddressService,
  private val accountDao: AccountDao
) : AddressRepository {

  override suspend fun getAddresses(): Resource<List<Address>> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          addressService.getAddresses(token).let { response ->
            Resource.Success(response)
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun getAddress(addressId: Int): Resource<Address> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          addressService.getAddress(token, addressId).let { response ->
            Resource.Success(response)
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun createAddress(request: CreateAddressRequest): Resource<Address> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          addressService.createAddress(token, request).let { response ->
            Resource.Success(response)
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun updateAddress(addressId: Int, request: CreateAddressRequest): Resource<Address> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          addressService.updateAddress(token, addressId, request).let { response ->
            Resource.Success(response)
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun removeAddress(addressId: Int): Resource<Nothing> {
    return try {
      getTokenOrNull().let { token ->
        if (token != null) {
          addressService.removeAddress(token, addressId).let { response ->
            if (response.success) {
              Resource.Success()
            } else {
              Resource.Failed(code = Constants.CODE_INTERNAL_SERVER_ERROR)
            }
          }
        } else {
          Resource.Failed(code = Constants.CODE_UNAUTHORIZED)
        }
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  override suspend fun getKecamatan(): Resource<List<Kecamatan>> {
    return try {
//      indonesianAddressService.getKecamatan().let { response ->
//        Resource.Success(response.kecamatan)
//      }
      Resource.Success(DummyKecamatan.getList())
    } catch (e: Exception) {
      Resource.Failed(message = e.message.toString())
    }
  }

  override suspend fun getKelurahan(kecamatanId: Long): Resource<List<Kelurahan>> {
    return try {
      indonesianAddressService.getKelurahan(kecamatanId).let { response ->
        Resource.Success(response.kelurahan)
      }
    } catch (e: Exception) {
      Resource.Failed(message = e.message.toString())
    }
  }

  private suspend fun getTokenOrNull(): String? = accountDao.getSingleAccount()?.token?.makeBearerToken()

}