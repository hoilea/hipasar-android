package com.uberando.hipasar.data.repository

import com.uberando.hipasar.data.Resource
import com.uberando.hipasar.entity.Address
import com.uberando.hipasar.entity.DeliveryTime
import com.uberando.hipasar.entity.Fee
import java.time.LocalDate
import java.util.*

interface CheckoutRepository {
  suspend fun getAddresses(): Resource<List<Address>>
  suspend fun getFees(): Resource<Fee>
  suspend fun getDeliveryTime(): Resource<List<DeliveryTime>>
  suspend fun getDeliveryDate(): Resource<List<String>>
}