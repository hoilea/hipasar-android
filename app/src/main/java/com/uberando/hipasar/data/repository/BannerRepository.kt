package com.uberando.hipasar.data.repository

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.entity.Banner

interface BannerRepository {
  suspend fun getBanners(): Resource<List<Banner>>
}