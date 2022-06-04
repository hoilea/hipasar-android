package com.uberando.hipasar.data.source

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.data.repository.FileRepository
import com.uberando.hipasar.data.source.remote.FileService
import com.uberando.hipasar.entity.Image
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class FileDataSource @Inject constructor(
  private val fileService: FileService
) : FileRepository {

  override suspend fun uploadImage(data: File): Resource<Image> {
    return try {
      fileService.uploadImage(createMultipartBody(data)).let { response ->
        Resource.Success(response)
      }
    } catch (e: Exception) {
      Resource.Failed()
    }
  }

  private fun createMultipartBody(file: File) =
    MultipartBody.Part.createFormData(
      FILE_NAME,
      file.name,
      file.asRequestBody(MEDIA_TYPE.toMediaType())
    )

  companion object {
    private const val FILE_NAME = "file"
    private const val MEDIA_TYPE = "image/png"
  }

}