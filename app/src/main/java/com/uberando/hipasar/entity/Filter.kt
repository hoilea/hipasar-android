package com.uberando.hipasar.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Filter(
  val code: Int,
  val name: String,
  /** hold order count on some filter */
  var orderCount: Int = 0
) : Parcelable {

  fun getOrderCountString() = orderCount.toString()

}
