package com.uberando.hipasar.data.source.dummy

import com.uberando.hipasar.entity.Kecamatan

object DummyKecamatan {

  private val dummies = listOf(
    Kecamatan(
      id = 3213060,
      cityId = "3213",
      name = "Cibogo"
    ),
    Kecamatan(
      id = 3213070,
      cityId = "3213",
      name = "Subang"
    ),
    Kecamatan(
      id = 3213081,
      cityId = "3213",
      name = "Dawuan"
    ),
    Kecamatan(
      id = 3213140,
      cityId = "3213",
      name = "Pagaden"
    ),
    Kecamatan(
      id = 3213141,
      cityId = "3213",
      name = "Pagaden Barat"
    )
  )

  fun getList() = dummies

}