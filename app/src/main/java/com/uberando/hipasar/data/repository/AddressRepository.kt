package com.uberando.hipasar.data.repository

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.entity.Address
import com.uberando.hipasar.entity.Kecamatan
import com.uberando.hipasar.entity.Kelurahan
import com.uberando.hipasar.entity.request.address.CreateAddressRequest

interface AddressRepository {
  suspend fun getAddresses(): Resource<List<Address>>
  suspend fun getAddress(addressId: Int): Resource<Address>
  suspend fun createAddress(request: CreateAddressRequest): Resource<Address>
  suspend fun updateAddress(addressId: Int, request: CreateAddressRequest): Resource<Address>
  suspend fun removeAddress(addressId: Int): Resource<Nothing>
  suspend fun getKecamatan(): Resource<List<Kecamatan>>
  suspend fun getKelurahan(kecamatanId: Long): Resource<List<Kelurahan>>
}