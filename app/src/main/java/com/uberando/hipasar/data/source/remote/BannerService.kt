package com.uberando.hipasar.data.source.remote

import com.uberando.hipasar.entity.Banner
import retrofit2.http.GET

interface BannerService {

  @GET("banners")
  suspend fun getBanners(): List<Banner>

}