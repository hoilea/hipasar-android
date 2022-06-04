package com.uberando.hipasar.data.source.remote

import com.uberando.hipasar.entity.KecamatanWrapper
import com.uberando.hipasar.entity.KelurahanWrapper
import retrofit2.http.GET
import retrofit2.http.Query

interface IndonesianAddressService {

  @GET("kecamatan")
  suspend fun getKecamatan(
    @Query("id_kota") kotaId: Int = 3213
  ): KecamatanWrapper

  @GET("kelurahan")
  suspend fun getKelurahan(
    @Query("id_kecamatan") kecamatanId: Long
  ): KelurahanWrapper

}