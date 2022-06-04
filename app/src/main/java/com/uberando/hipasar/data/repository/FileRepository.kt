package com.uberando.hipasar.data.repository

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.entity.Image
import java.io.File

interface FileRepository {
  suspend fun uploadImage(data: File): Resource<Image>
}