package com.uberando.hipasar.data.source.remote

import com.uberando.hipasar.entity.Image
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileService {

  @Multipart
  @POST("filestores")
  suspend fun uploadImage(
    @Part file: MultipartBody.Part
  ): Image

}