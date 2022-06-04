package com.uberando.hipasar.entity

sealed class PaymentMethodType {
  data class TitleType(val title: String) : PaymentMethodType()
  data class ContentType(val data: PaymentMethod) : PaymentMethodType()
}