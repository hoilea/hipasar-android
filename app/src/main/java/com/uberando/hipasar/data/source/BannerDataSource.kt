package com.uberando.hipasar.data.source

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.BannerRepository
import com.uberando.hipasar.data.source.remote.BannerService
import com.uberando.hipasar.entity.Banner
import javax.inject.Inject

class BannerDataSource @Inject constructor(
  private val bannerService: BannerService
) : BannerRepository {

  override suspend fun getBanners(): Resource<List<Banner>> {
    return try {
      bannerService.getBanners().let { response ->
        Resource.Success(response)
      }
    } catch (e: Exception) {
      Resource.Failed(message = e.message.toString())
    }
  }

}