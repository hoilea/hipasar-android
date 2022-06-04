package com.uberando.hipasar.util

import java.text.SimpleDateFormat
import java.util.*


object DateUtil {

  private const val SERVER_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS"
  private const val ORDER_PATTERN = "d MMM yyyy, hh:mm"
  private const val DATE_ONLY_PATTERN = "d MMM"
  private const val DELIVERY_DATE_PATTERN = "dd MMMM yyyy"

  fun parseToOrderFormat(datetime: String): String {
    val dateFormat = getServerDateFormat()
    val parsedDate = dateFormat.parse(datetime) ?: Date()
    val orderFormat = SimpleDateFormat(ORDER_PATTERN, Locale.getDefault())
    return orderFormat.format(parsedDate)
  }

  fun parseToDateOnlyFormat(datetime: String?): String {
    val dateFormat = getServerDateFormat()
    val parsedDate = datetime?.let { dateFormat.parse(datetime) } ?: Date()
    val dateOnlyFormat = SimpleDateFormat(DATE_ONLY_PATTERN, Locale.getDefault())
    return dateOnlyFormat.format(parsedDate)
  }

  fun parseToDeliveryDateFormat(datetime: String?): String {
    val dateFormat = getServerDateFormat()
    val parsedDate = datetime?.let { dateFormat.parse(datetime) } ?: Date()
    val dateOnlyFormat = SimpleDateFormat(DELIVERY_DATE_PATTERN, Locale.getDefault())
    return dateOnlyFormat.format(parsedDate)
  }

  private fun getServerDateFormat() =
    SimpleDateFormat(SERVER_PATTERN, Locale.getDefault())

  fun getDeliveryDateInterval(currentDate: Date, interval: Int): List<String> {
    var dateHolder = currentDate.plus(1)
    val date = mutableListOf<String>()
    for (i in 0 until interval) {
      date.add(dateHolder.toDeliveryPattern())
      dateHolder = dateHolder.plus(1)
    }
    return date
  }

  private fun Date.toDeliveryPattern(): String {
    val sdf = SimpleDateFormat(DELIVERY_DATE_PATTERN, Locale.getDefault())
    return sdf.format(this)
  }

}