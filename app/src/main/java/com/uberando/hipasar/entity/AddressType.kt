package com.uberando.hipasar.entity

sealed class AddressType {
  data class PrimaryType(val address: Address) : AddressType()
  data class NonPrimaryType(val address: Address) : AddressType()
}
