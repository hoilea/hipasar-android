package com.uberando.hipasar.entity

sealed class OrderType {
  data class TypePending(val order: Order) : OrderType()
  data class TypeAdminConfirmation(val order: Order) : OrderType()
  data class TypeOnProcess(val order: Order) : OrderType()
  data class TypeOnDelivery(val order: Order) : OrderType()
  data class TypeArrived(val order: Order) : OrderType()
  data class TypeFinished(val order: Order) : OrderType()
  data class TypeCancellationRequest(val order: Order) : OrderType()
  data class TypeCancelled(val order: Order) : OrderType()
}