package com.uberando.hipasar.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
  val id: String,
  val path: String
): Parcelable
